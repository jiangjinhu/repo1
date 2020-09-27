package com.itheima.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * @description:
 * @author: 蒋金虎
 * @time: 2020/9/26 17:12
 */
public class KafkaProducerClientTest {
    public static void main(String[] args) {
        //构建PROP对象，管理生产者的配置
        Properties props = new Properties();
        //指定连接的Kafka的地址
        props.put("bootstrap.servers", "node1:9092,node2:9092,node3:9092");
        props.put("acks", "all");
        //如果发送失败，重试的次数
        props.put("retries", 0);
        //每次从缓存中发送的批次的大小
        props.put("batch.size", 16384);
        //间隔时间
        props.put("linger.ms", 1);
        //生产数据的 缓存
        props.put("buffer.memory", 33554432);
        //序列化机制：Kafka也是以KV形式进行数据存储，K可以没有，写入的数据是Value
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        //自定义分区方式
        props.put("partitioner.class","com.itheima.kafka.partition.UserPartition");
        //创建一个Kafka的生产者对象，加载Kafka生产者的配置
        Producer<String, String> producer = new KafkaProducer<String, String>(props);
        //构建循环，模拟不断产生新的数据
        for (int i = 0; i < 10; i++) {
            //生产者对象 调用send方法往Topic中放数据
            //方式一：new ProducerRecord<String, String>(Topic的名称, K, V)
//            producer.send(new ProducerRecord<String, String>("bigdata2301", Integer.toString(i), "itcast"+i));
            //方式二:指定Topic和Value，没有Key
//            producer.send(new ProducerRecord<String, String>("bigdata2301","itcast"+i));
            //方式三：指定写入某一个分区
            //producer.send(new ProducerRecord<String, String>("bigdata2301", 0, Integer.toString(i), "itcast" + i));
            //方式四：自定义随机分区
            producer.send(new ProducerRecord<String, String>("bigdata2302",Integer.toString(i),"itcast" + i));
        }
        //关闭生产者
        producer.close();
    }
}
