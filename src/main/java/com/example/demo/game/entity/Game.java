package com.example.demo.game.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game  implements Serializable {

    private static final long serialVersionUID = 1829866764473132684L;
    private  int idgame=0;
    private  String gameName;
    private  String gameType;
    private  String cover;
}
