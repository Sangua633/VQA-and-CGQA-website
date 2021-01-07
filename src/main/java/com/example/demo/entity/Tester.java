package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tester implements Serializable {
    private static final long serialVersionUID= 4522943071576672084L;
    private int idtester;
    private String username;
    private String password;
    private String permission="editor";
    private String fullname;
    private String sex;
    private int age;
    private String job;
    private String telephone;
    private  int isUsed=0;
}
