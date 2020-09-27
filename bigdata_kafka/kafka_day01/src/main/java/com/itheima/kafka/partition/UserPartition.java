package com.itheima.kafka.partition;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;
import java.util.Random;

/**
 * @description:用于实现Kafka中的自定义分区
 * @author: 蒋金虎
 * @time: 2020/9/26 17:16
 */
public class UserPartition implements Partitioner {
    /**
     * 用于返回当前数据对应的分区编号
     * @param topic：当前数据所对应的Topic名称
     * @param key：K
     * @param keyBytes：K的字节类型
     * @param value：V
     * @param valueBytes：V的字节类型
     * @param cluster：集群信息
     * @return
     */
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        //获取分区的个数
        Integer forTotic = cluster.partitionCountForTopic(topic);
        //构建一个随机值对象
        Random random = new Random();
        //指定random取值
        int i = random.nextInt(forTotic);
        return i;
    }

    public void close() {
        //释放资源
    }

    public void configure(Map<String, ?> map) {
        //获取配置的方法
    }
}
