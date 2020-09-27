package com.itheima.kafka.offset;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.*;

/**
 * @description:手动管理每个分区的offset
 * @author: 蒋金虎
 * @time: 2020/9/26 17:39
 */
public class KafkaConsumerManagerPartitionOffset {
    public static void main(String[] args) {
        //构建PROP用于管理配置
        Properties props = new Properties();
        //指定Kafka的服务端地址
        props.put("bootstrap.servers", "node1:9092,node2:9092,node3:9092");
        //指定消费者组的id
        props.put("group.id", "test");
        //是否让kafka自动提交消费的偏移量
        props.put("enable.auto.commit", "true");
        //按照固定时间间隔来记录的
//        props.put("auto.commit.interval.ms", "1000");
        //反序列化
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        //构建一个Kafka消费者对象
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String,String>(props);
        //指定要消费的Topic
//        consumer.subscribe(Arrays.asList("bigdata2301"));
        //指定分区消费
        TopicPartition part0 = new TopicPartition("bigdata2302", 0);
        TopicPartition part1 = new TopicPartition("bigdata2302", 1);
        TopicPartition part2 = new TopicPartition("bigdata2302", 2);
        consumer.assign(Arrays.asList(part0,part1,part2));
        //源源不断的进行消费
        while (true) {
            //消费者拉取对应Topic的数据
            ConsumerRecords<String, String> records = consumer.poll(100);
            //todo:1-将所有数据按照分区拆分开来，对每个分区的数据进行处理
            Set<TopicPartition> partitions = records.partitions();
            //迭代每个分区，对 每个分区的数据进行处理
            for (TopicPartition partition : partitions) {
                //定义偏移量
                long offset = 0;
                //拿到每个分区的信息，根据分区的信息到record中取属于这个分区的数据
                List<ConsumerRecord<String, String>> partRecored = records.records(partition);
                //对这个分区的数据进行处理
                //迭代取出每一条数据
                for (ConsumerRecord<String, String> record : records){
                    //获取这条数据属于哪个Topic
                    String topic = record.topic();
                    //获取这条数据属于这个topic的哪个分区
                    int part = record.partition();
                    //获取这个数据在这个分区中对应的偏移量
                    offset = record.offset();
                    //获取Key
                    String key = record.key();
                    //获取value
                    String value = record.value();
                    System.out.println(topic+"\t"+part+"\t"+offset+"\t"+key+"" + "\t"+value);
                }
                //todo:2-这个分区的数据处理完整，提交这个分区的offset
                //partition：要记录的分区的信息，哪个topic的哪个分区
                //OffsetAndMetadata：当前要记录的分区消费的偏移量,本次消费的最高的offset+1
                Map<TopicPartition, OffsetAndMetadata> offsets = Collections.singletonMap(partition, new OffsetAndMetadata(offset + 1));
                consumer.commitSync(offsets);
                //todo:3-工作中的方式：Kafka的自动提交与手动提交不关心，我们自己存储一份offset
                //我们会将每次消费成功的每个分区的offset存储在MySQL中，如果消费者故障，每次程序重启，读取MySQL获取上次的偏移量
            }
        }
    }
}
