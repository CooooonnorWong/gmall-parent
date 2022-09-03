package com.atguigu.gmall.search.bean;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author Connor
 * @date 2022/9/3
 */
@Document(indexName = "person", shards = 1, replicas = 1)
public class Person {
    @Id
    private Long id;

    @Field(value = "age", type = FieldType.Keyword)
    private Integer age;

    @Field(value = "address", type = FieldType.Text)
    private String address;

    @Field(value = "firstName", type = FieldType.Keyword)
    private String firstName;

    @Field(value = "lastName", type = FieldType.Keyword)
    private String lastName;

    @Field("description")
    private String description;

    public Person() {
    }

    public Person(Long id, Integer age, String address, String firstName, String lastName, String description) {
        this.id = id;
        this.age = age;
        this.address = address;
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", age=" + age +
                ", address='" + address + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
