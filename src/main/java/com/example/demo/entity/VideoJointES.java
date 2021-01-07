package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/***
 * 具有video类和EncodingScheme类的属性
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoJointES implements Serializable {
    private static final long serialVersionUID = -8749574389183531348L;
    private List<String> videoNames;
    private EncodingScheme encodingScheme;
}
