package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncodingSchemeVideo implements Serializable {

    private static final long serialVersionUID = 2256572560780694545L;
    private  int idvt;
    private EncodingScheme es;
    private Video video;
}
