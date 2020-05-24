package netty.protocoltcp;

import lombok.Builder;
import lombok.Data;

/**
 * Description :   .
 *
 * @author : Phoebe
 * @date : Created in 2020/5/24
 */
@Data
@Builder
public class MessageProtocol {

  /**
   * 发送的数据长度
   */
  private int len;

  /**
   * 要发送的数据
   */
  private byte[] content;

}
