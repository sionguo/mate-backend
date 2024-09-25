package cn.guoxy.mate.board;

import cn.guoxy.mate.board.dto.BoardDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 板控制器
 *
 * @author Guo XiaoYong
 */
@RestController
@RequestMapping("boards")
@Tag(name = "boards", description = "白板控制器")
public class BoardController {
  @PostMapping
  @Operation(summary = "创建")
  public ResponseEntity<BoardDto> create(@RequestBody BoardDto boardDto) {
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "删除")
  public ResponseEntity<BoardDto> delete(@PathVariable String id) {
    return ResponseEntity.ok().build();
  }

  @PutMapping("/{id}")
  @Operation(summary = "修改")
  public ResponseEntity<BoardDto> modify(@PathVariable String id, @RequestBody BoardDto boardDto) {
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{id}")
  @Operation(summary = "获取")
  public ResponseEntity<BoardDto> get(@PathVariable String id) {
    return ResponseEntity.ok().build();
  }

  @GetMapping
  @Operation(summary = "列表")
  public ResponseEntity<BoardDto> list() {
    return ResponseEntity.ok().build();
  }
}
