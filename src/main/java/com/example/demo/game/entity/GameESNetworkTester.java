package com.example.demo.game.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameESNetworkTester implements Serializable {

    private static final long serialVersionUID = -8471504265531107195L;
    private List<String> usernames;
    private List<Integer> idVideoNetworks;
    private List<Integer> idgameEncodingSchemes;
}
