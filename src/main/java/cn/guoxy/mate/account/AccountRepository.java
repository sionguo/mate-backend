package cn.guoxy.mate.account;

import cn.guoxy.mate.data.BaseRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户存储库
 *
 * @see BaseRepository
 * @author Guo XiaoYong
 */
@Repository
public interface AccountRepository extends BaseRepository<Account> {
  /**
   * 按用户名查找
   *
   * @param username 用户名
   * @return {@code Account }
   */
  Account findByUsername(String username);
}
