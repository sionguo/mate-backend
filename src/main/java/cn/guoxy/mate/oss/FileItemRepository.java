package cn.guoxy.mate.oss;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 文件项存储库
 *
 * @see JpaRepository
 * @author Guo XiaoYong
 */
@Repository
public interface FileItemRepository extends JpaRepository<FileItem, String> {}
