package cn.guoxy.mate.common.tsid;

import static cn.guoxy.mate.common.tsid.Tsid.RANDOM_BITS;
import static cn.guoxy.mate.common.tsid.Tsid.RANDOM_MASK;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

/**
 * A factory that actually generates Time-Sorted Unique Identifiers (TSID).
 *
 * <p>This factory is used by the {@link TsidCreator} utility.
 *
 * <p>Most people just need {@link TsidCreator}. However, you can use this class if you need to make
 * some customizations, for example changing the default {@link SecureRandom} random generator to a
 * faster pseudo-random generator.
 *
 * <p>If a system property "tsidcreator.node" or environment variable "TSIDCREATOR_NODE" is defined,
 * its value is utilized as node identifier. One of them <b>should</b> be defined to embed a machine
 * ID in the generated TSID in order to avoid TSID collisions. Using that property or variable is
 * <b>highly recommended</b>. If no property or variable is defined, a random node ID is generated
 * at initialization.
 *
 * <p>If a system property "tsidcreator.node.count" or environment variable "TSIDCREATOR_NODE_COUNT"
 * is defined, its value is utilized by the constructors of this class to adjust the amount of bits
 * needed to embed the node ID. For example, if the number 50 is given, the node bit amount is
 * adjusted to 6, which is the minimum number of bits to accommodate 50 nodes. If no property or
 * variable is defined, the number of bits reserved for node ID is set to 10, which can accommodate
 * 1024 nodes.
 *
 * <p>A "node" as we call it in this library can be a physical machine, a virtual machine, a
 * container, a k8s pod, a running process, etc.
 *
 * <p>This class <b>should</b> be used as a singleton. Make sure that you create and reuse a single
 * instance of {@link TsidFactory} per node in your distributed system.
 */
public final class TsidFactory {

  static final int NODE_BITS_256 = 8;
  static final int NODE_BITS_1024 = 10;
  static final int NODE_BITS_4096 = 12;
  private final int node;
  private final int nodeBits;
  private final int counterBits;
  private final int nodeMask;
  private final int counterMask;
  private final long customEpoch;
  private final LongSupplier timeFunction;
  private final IRandom random;
  private final int randomBytes;
  private final ReentrantLock lock = new ReentrantLock();
  private int counter;
  private long lastTime;

  // ******************************
  // Constructors
  // ******************************

  /**
   * It builds a new factory.
   *
   * <p>The node identifier provided by the "tsidcreator.node" system property or the
   * "TSIDCREATOR_NODE" environment variable is embedded in the generated TSIDs in order to avoid
   * collisions. It is <b>highly recommended</b> defining that property or variable. Otherwise the
   * node identifier will be randomly chosen.
   *
   * <p>If a system property "tsidcreator.node.count" or environment variable
   * "TSIDCREATOR_NODE_COUNT" is defined, its value is used to adjust the node bits amount.
   */
  public TsidFactory() {
    this(builder());
  }

  /**
   * It builds a new factory.
   *
   * <p>The node identifier provided by parameter is embedded in the generated TSIDs in order to
   * avoid collisions.
   *
   * <p>If a system property "tsidcreator.node.count" or environment variable
   * "TSIDCREATOR_NODE_COUNT" is defined, its value is used to adjust the node bits amount.
   *
   * @param node the node identifier
   */
  public TsidFactory(int node) {
    this(builder().withNode(node));
  }

  /**
   * It builds a generator with the given builder.
   *
   * @param builder a builder instance
   */
  private TsidFactory(Builder builder) {

    // setup the custom epoch, the node bits, etc
    this.customEpoch = builder.getCustomEpoch();
    this.nodeBits = builder.getNodeBits();
    this.random = builder.getRandom();
    this.timeFunction = builder.getTimeFunction();

    // setup constants that depend on node bits
    this.counterBits = RANDOM_BITS - nodeBits;
    this.counterMask = RANDOM_MASK >>> nodeBits;
    this.nodeMask = RANDOM_MASK >>> counterBits;

    // setup how many bytes to get from the random function
    this.randomBytes = ((this.counterBits - 1) / 8) + 1;

    // setup the node identifier
    this.node = builder.getNode() & nodeMask;

    // finally initialize inner state
    this.lastTime = 0L; // 1970-01-01
    this.counter = getRandomCounter();
  }

  /**
   * Returns a new factory for up to 256 nodes and 16384 ID/ms.
   *
   * @return {@link TsidFactory}
   */
  public static TsidFactory newInstance256() {
    return TsidFactory.builder().withNodeBits(NODE_BITS_256).build();
  }

  /**
   * Returns a new factory for up to 256 nodes and 16384 ID/ms.
   *
   * @param node the node identifier
   * @return {@link TsidFactory}
   */
  public static TsidFactory newInstance256(int node) {
    return TsidFactory.builder().withNodeBits(NODE_BITS_256).withNode(node).build();
  }

  /**
   * Returns a new factory for up to 1024 nodes and 4096 ID/ms.
   *
   * <p>It is equivalent to {@code new TsidFactory()}.
   *
   * @return {@link TsidFactory}
   */
  public static TsidFactory newInstance1024() {
    return TsidFactory.builder().withNodeBits(NODE_BITS_1024).build();
  }

  /**
   * Returns a new factory for up to 1024 nodes and 4096 ID/ms.
   *
   * <p>It is equivalent to {@code new TsidFactory(int)}.
   *
   * @param node the node identifier
   * @return {@link TsidFactory}
   */
  public static TsidFactory newInstance1024(int node) {
    return TsidFactory.builder().withNodeBits(NODE_BITS_1024).withNode(node).build();
  }

  /**
   * Returns a new factory for up to 4096 nodes and 1024 ID/ms.
   *
   * @return {@link TsidFactory}
   */
  public static TsidFactory newInstance4096() {
    return TsidFactory.builder().withNodeBits(NODE_BITS_4096).build();
  }

  /**
   * Returns a new factory for up to 4096 nodes and 1024 ID/ms.
   *
   * @param node the node identifier
   * @return {@link TsidFactory}
   */
  public static TsidFactory newInstance4096(int node) {
    return TsidFactory.builder().withNodeBits(NODE_BITS_4096).withNode(node).build();
  }

  // ******************************
  // Public methods
  // ******************************

  /**
   * Returns a builder object.
   *
   * <p>It is used to build a custom {@link TsidFactory}.
   *
   * @return a builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Returns a TSID.
   *
   * @return a TSID.
   */
  public Tsid create() {
    lock.lock();
    try {
      final long _time = getTime() << RANDOM_BITS;
      final long _node = (long) this.node << this.counterBits;
      final long _counter = (long) this.counter & this.counterMask;

      return new Tsid(_time | _node | _counter);
    } finally {
      lock.unlock();
    }
  }

  /**
   * Returns the current time.
   *
   * <p>If the current time is equal to the previous time, the counter is incremented by one.
   * Otherwise the counter is reset to a random value.
   *
   * <p>The maximum number of increment operations depend on the counter bits. For example, if the
   * counter bits is 12, the maximum number of increment operations is 2^12 = 4096.
   *
   * @return the current time
   */
  private long getTime() {

    long time = timeFunction.getAsLong();

    if (time <= this.lastTime) {
      this.counter++;
      // Carry is 1 if an overflow occurs after ++.
      int carry = this.counter >>> this.counterBits;
      this.counter = this.counter & this.counterMask;
      time = this.lastTime + carry; // increment time
    } else {
      // If the system clock has advanced as expected,
      // simply reset the counter to a new random value.
      this.counter = this.getRandomCounter();
    }

    // save current time
    this.lastTime = time;

    // adjust to the custom epoch
    return time - this.customEpoch;
  }

  /**
   * Returns a random counter value from 0 to 0x3fffff (2^22-1 = 4,194,303).
   *
   * <p>The counter maximum value depends on the node identifier bits. For example, if the node
   * identifier has 10 bits, the counter has 12 bits.
   *
   * @return a number
   */
  private int getRandomCounter() {

    if (random instanceof ByteRandom) {

      final byte[] bytes = random.nextBytes(this.randomBytes);

      switch (bytes.length) {
        case 1:
          return (bytes[0] & 0xff) & this.counterMask;
        case 2:
          return (((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff)) & this.counterMask;
        default:
          return (((bytes[0] & 0xff) << 16) | ((bytes[1] & 0xff) << 8) | (bytes[2] & 0xff))
              & this.counterMask;
      }

    } else {
      return random.nextInt() & this.counterMask;
    }
  }

  // ******************************
  // Package-private inner classes
  // ******************************

  static interface IRandom {

    public int nextInt();

    public byte[] nextBytes(int length);
  }

  /**
   * A nested class that builds custom TSID factories.
   *
   * <p>It is used to setup a custom {@link TsidFactory}.
   */
  public static class Builder {

    private Integer node;
    private Integer nodeBits;
    private Long customEpoch;
    private IRandom random;
    private LongSupplier timeFunction;

    /**
     * Set the node identifier.
     *
     * @param node a number that must be between 0 and 2^nodeBits-1.
     * @return {@link Builder}
     * @throws IllegalArgumentException if the node identifier is out of the range [0, 2^nodeBits-1]
     *     when {@code build()} is invoked
     */
    public Builder withNode(Integer node) {
      this.node = node;
      return this;
    }

    /**
     * Set the node identifier bits length.
     *
     * @param nodeBits a number that must be between 0 and 20.
     * @return {@link Builder}
     * @throws IllegalArgumentException if the node bits are out of the range [0, 20] when {@code
     *     build()} is invoked
     */
    public Builder withNodeBits(Integer nodeBits) {
      this.nodeBits = nodeBits;
      return this;
    }

    /**
     * Set the custom epoch.
     *
     * @param customEpoch an instant that represents the custom epoch.
     * @return {@link Builder}
     */
    public Builder withCustomEpoch(Instant customEpoch) {
      this.customEpoch = customEpoch.toEpochMilli();
      return this;
    }

    /**
     * Set the random generator.
     *
     * <p>The random generator is used to create a random function that is used to reset the counter
     * when the millisecond changes.
     *
     * @param random a {@link Random} generator
     * @return {@link Builder}
     */
    public Builder withRandom(Random random) {
      if (random != null) {
        if (random instanceof SecureRandom) {
          this.random = new ByteRandom(random);
        } else {
          this.random = new IntRandom(random);
        }
      }
      return this;
    }

    /**
     * Set the random function.
     *
     * <p>The random function is used to reset the counter when the millisecond changes.
     *
     * @param randomFunction a random function that returns a integer value
     * @return {@link Builder}
     */
    public Builder withRandomFunction(IntSupplier randomFunction) {
      this.random = new IntRandom(randomFunction);
      return this;
    }

    /**
     * Set the random function.
     *
     * <p>The random function must return a byte array of a given length.
     *
     * <p>The random function is used to reset the counter when the millisecond changes.
     *
     * <p>Despite its name, the random function MAY return a fixed value, for example, if your app
     * requires the counter to be reset to ZERO whenever the millisecond changes, like Twitter
     * Snowflakes, this function should return an array filled with ZEROS.
     *
     * @param randomFunction a random function that returns a byte array
     * @return {@link Builder}
     */
    public Builder withRandomFunction(IntFunction<byte[]> randomFunction) {
      this.random = new ByteRandom(randomFunction);
      return this;
    }

    /**
     * Set the clock to be used in tests.
     *
     * <p>{@link Clock} is too complicated. Use {@link #withTimeFunction(LongSupplier)} instead.
     *
     * @param clock a clock
     * @return {@link Builder}
     */
    public Builder withClock(Clock clock) {
      this.timeFunction = () -> clock.millis();
      return this;
    }

    /**
     * Set the time function to be used in tests.
     *
     * @param timeFunction a function that returns the current time in milliseconds, measured from
     *     the UNIX epoch of 1970-01-01T00:00Z (UTC)
     * @return {@link Builder}
     */
    public Builder withTimeFunction(LongSupplier timeFunction) {
      this.timeFunction = timeFunction;
      return this;
    }

    /**
     * Get the node identifier.
     *
     * @return a number
     * @throws IllegalArgumentException if the node is out of range
     */
    protected Integer getNode() {

      final int max = (1 << nodeBits) - 1;

      if (this.node == null) {
        if (Settings.getNode() != null) {
          // use property or variable
          this.node = Settings.getNode();
        } else {
          // use random node identifier
          this.node = this.random.nextInt() & max;
        }
      }

      if (node < 0 || node > max) {
        throw new IllegalArgumentException(
            String.format("Node ID out of range [0, %s]: %s", max, node));
      }

      return this.node;
    }

    /**
     * Get the node identifier bits length within the range 0 to 20.
     *
     * @return a number
     * @throws IllegalArgumentException if the node bits are out of range
     */
    protected Integer getNodeBits() {

      if (this.nodeBits == null) {
        if (Settings.getNodeCount() != null) {
          // use property or variable
          this.nodeBits = (int) Math.ceil(Math.log(Settings.getNodeCount()) / Math.log(2));
        } else {
          // use default bit length: 10 bits
          this.nodeBits = TsidFactory.NODE_BITS_1024;
        }
      }

      if (nodeBits < 0 || nodeBits > 20) {
        throw new IllegalArgumentException(
            String.format("Node bits out of range [0, 20]: %s", nodeBits));
      }

      return this.nodeBits;
    }

    /**
     * Gets the custom epoch.
     *
     * @return a number
     */
    protected Long getCustomEpoch() {
      if (this.customEpoch == null) {
        this.customEpoch = Tsid.TSID_EPOCH; // 2020-01-01
      }
      return this.customEpoch;
    }

    /**
     * Gets the random generator.
     *
     * @return a random generator
     */
    protected IRandom getRandom() {
      if (this.random == null) {
        this.withRandom(new SecureRandom());
      }
      return this.random;
    }

    /**
     * Gets the time function to be used in tests.
     *
     * @return a time function
     */
    protected LongSupplier getTimeFunction() {
      if (this.timeFunction == null) {
        this.withTimeFunction(System::currentTimeMillis);
      }
      return this.timeFunction;
    }

    /**
     * Returns a build TSID factory.
     *
     * @return {@link TsidFactory}
     * @throws IllegalArgumentException if the node is out of range
     * @throws IllegalArgumentException if the node bits are out of range
     */
    public TsidFactory build() {
      return new TsidFactory(this);
    }
  }

  static class IntRandom implements IRandom {

    private final IntSupplier randomFunction;

    public IntRandom() {
      this(newRandomFunction(null));
    }

    public IntRandom(Random random) {
      this(newRandomFunction(random));
    }

    public IntRandom(IntSupplier randomFunction) {
      this.randomFunction = randomFunction != null ? randomFunction : newRandomFunction(null);
    }

    protected static IntSupplier newRandomFunction(Random random) {
      final Random entropy = random != null ? random : new SecureRandom();
      return entropy::nextInt;
    }

    @Override
    public int nextInt() {
      return randomFunction.getAsInt();
    }

    @Override
    public byte[] nextBytes(int length) {

      int shift = 0;
      long random = 0;
      final byte[] bytes = new byte[length];

      for (int i = 0; i < length; i++) {
        if (shift < Byte.SIZE) {
          shift = Integer.SIZE;
          random = randomFunction.getAsInt();
        }
        shift -= Byte.SIZE; // 56, 48, 40...
        bytes[i] = (byte) (random >>> shift);
      }

      return bytes;
    }
  }

  static class ByteRandom implements IRandom {

    private final IntFunction<byte[]> randomFunction;

    public ByteRandom() {
      this(newRandomFunction(null));
    }

    public ByteRandom(Random random) {
      this(newRandomFunction(random));
    }

    public ByteRandom(IntFunction<byte[]> randomFunction) {
      this.randomFunction = randomFunction != null ? randomFunction : newRandomFunction(null);
    }

    protected static IntFunction<byte[]> newRandomFunction(Random random) {
      final Random entropy = random != null ? random : new SecureRandom();
      return (final int length) -> {
        final byte[] bytes = new byte[length];
        entropy.nextBytes(bytes);
        return bytes;
      };
    }

    @Override
    public int nextInt() {
      int number = 0;
      byte[] bytes = this.randomFunction.apply(Integer.BYTES);
      for (int i = 0; i < Integer.BYTES; i++) {
        number = (number << 8) | (bytes[i] & 0xff);
      }
      return number;
    }

    @Override
    public byte[] nextBytes(int length) {
      return this.randomFunction.apply(length);
    }
  }

  static class Settings {

    static final String NODE = "tsidcreator.node";
    static final String NODE_COUNT = "tsidcreator.node.count";

    private Settings() {}

    public static Integer getNode() {
      return getPropertyAsInteger(NODE);
    }

    public static Integer getNodeCount() {
      return getPropertyAsInteger(NODE_COUNT);
    }

    static Integer getPropertyAsInteger(String property) {
      try {
        return Integer.decode(getProperty(property));
      } catch (NumberFormatException | NullPointerException e) {
        return null;
      }
    }

    static String getProperty(String name) {

      String property = System.getProperty(name);
      if (property != null && !property.isEmpty()) {
        return property;
      }

      String variable = System.getenv(name.toUpperCase().replace(".", "_"));
      if (variable != null && !variable.isEmpty()) {
        return variable;
      }

      return null;
    }
  }
}
