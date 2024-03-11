package com.hixtrip.sample.client.sample.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author irving
 * @Date 2024-03-07 16:53:03
 */
@Getter
@Setter
@ToString
@AllArgsConstructor(staticName = "of")
public class OrderVO {

    private String id;
    private String skuId;
    private String msg;

}
