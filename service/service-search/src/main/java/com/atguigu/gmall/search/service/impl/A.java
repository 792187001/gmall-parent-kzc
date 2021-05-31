package com.atguigu.gmall.search.service.impl;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "index5",type = "goods",shards = 3,replicas = 1)
public class A {

    @Field(type = FieldType.Long,index = true)
    Long id;

    @Field(type = FieldType.Text,index =true)
    String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
