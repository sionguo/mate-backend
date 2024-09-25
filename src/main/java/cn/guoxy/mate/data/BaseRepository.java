package cn.guoxy.mate.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 基础存储库
 *
 * @see JpaRepository
 * @author Guo XiaoYong
 */
@NoRepositoryBean
public interface BaseRepository<T> extends JpaRepository<T, String> {}
