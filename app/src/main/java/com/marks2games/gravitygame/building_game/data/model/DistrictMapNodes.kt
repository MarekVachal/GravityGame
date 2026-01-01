package com.marks2games.gravitygame.building_game.data.model

data class DistrictConfig(
    val id: String,
    val posX: Float,
    val posY: Float,
    val connections: List<String>,
)

val smallPlanetDistrictNodes = listOf(
    DistrictConfig(
        id = "0",
        posX = 0.1f,
        posY = 0.55f,
        connections = listOf("1", "2", "3")
    ),
    DistrictConfig(
        id = "1",
        posX = 0.4f,
        posY = 1.0f,
        connections = listOf("0", "4", "5")
    ),
    DistrictConfig(
        id = "2",
        posX = 0.4f,
        posY = 0.55f,
        connections = listOf("0", "6", "4")
    ),
    DistrictConfig(
        id = "3",
        posX = 0.4f,
        posY = 0.1F,
        connections = listOf("0", "6", "5")
    ),
    DistrictConfig(
        id = "4",
        posX = 0.7f,
        posY = 1.0f,
        connections = listOf("1", "7", "2")
    ),
    DistrictConfig(
        id = "5",
        posX = 0.7f,
        posY = 0.1f,
        connections = listOf("1", "3", "7")
    ),
    DistrictConfig(
        id = "6",
        posX = 0.7f,
        posY = 0.55f,
        connections = listOf("2", "7", "3")
    ),
    DistrictConfig(
        id = "7",
        posX = 1.0f,
        posY = 0.55f,
        connections = listOf("6", "5", "4")
    )
)

val mediumPlanetDistrictNodes = listOf(
    DistrictConfig(
        id = "0",
        posX = 0.55f,
        posY = 0.1f,
        connections = listOf("1", "2", "3", "4", "5")
    ),
    DistrictConfig(
        id = "1",
        posX = 0.55f,
        posY = 0.4f,
        connections = listOf("0","7", "2", "3", "6")
    ),
    DistrictConfig(
        id = "2",
        posX = 0.775f,
        posY = 0.4f,
        connections = listOf("0","1", "4", "6", "8")
    ),
    DistrictConfig(
        id = "3",
        posX = 0.325f,
        posY = 0.4f,
        connections = listOf("0","1", "5", "9", "7")
    ),
    DistrictConfig(
        id = "4",
        posX = 1.0f,
        posY = 0.4f,
        connections = listOf("0", "2", "8", "10", "5")
    ),
    DistrictConfig(
        id = "5",
        posX = 0.1f,
        posY = 0.4f,
        connections = listOf("0", "3", "9", "4", "10")
    ),
    DistrictConfig(
        id = "6",
        posX = 0.55f,
        posY = 0.7f,
        connections = listOf("1", "2", "8", "11", "7")
    ),
    DistrictConfig(
        id = "7",
        posX = 0.325f,
        posY = 0.7f,
        connections = listOf("3","1", "6", "9", "11")
    ),
    DistrictConfig(
        id = "8",
        posX = 0.775f,
        posY = 0.7f,
        connections = listOf("2","4", "10", "11", "6")
    ),
    DistrictConfig(
        id = "9",
        posX = 0.1f,
        posY = 0.7f,
        connections = listOf("5","3", "7", "10", "11")
    ),
    DistrictConfig(
        id = "10",
        posX = 1.0f,
        posY = 0.7f,
        connections = listOf("4","5", "9", "8", "11")
    ),
    DistrictConfig(
        id = "11",
        posX = 0.55f,
        posY = 1.0f,
        connections = listOf("9","7", "6", "8", "10")
    )
)

val largePlanetDistrictNodes = listOf(
    DistrictConfig(
        id = "0",
        posX = 0.46f,
        posY = 0.1f,
        connections = listOf("1", "2", "3")
    ),
    DistrictConfig(
        id = "1",
        posX = 0.46f,
        posY = 0.28f,
        connections = listOf("0", "7", "4")
    ),
    DistrictConfig(
        id = "2",
        posX = 0.82f,
        posY = 0.28f,
        connections = listOf("0", "5", "8")
    ),
    DistrictConfig(
        id = "3",
        posX = 0.1f,
        posY = 0.28f,
        connections = listOf("0", "6", "9")
    ),
    DistrictConfig(
        id = "4",
        posX = 0.46f,
        posY = 0.46f,
        connections = listOf("1", "5", "10")
    ),
    DistrictConfig(
        id = "5",
        posX = 0.64f,
        posY = 0.46f,
        connections = listOf("4", "2", "11")
    ),
    DistrictConfig(
        id = "6",
        posX = 0.1f,
        posY = 0.46f,
        connections = listOf("3", "12", "7")
    ),
    DistrictConfig(
        id = "7",
        posX = 0.28f,
        posY = 0.46f,
        connections = listOf("6", "1", "13")
    ),
    DistrictConfig(
        id = "8",
        posX = 0.82f,
        posY = 0.46f,
        connections = listOf("2", "9", "14")
    ),
    DistrictConfig(
        id = "9",
        posX = 1.0f,
        posY = 0.46f,
        connections = listOf("8", "15", "3")
    ),
    DistrictConfig(
        id = "10",
        posX = 0.46f,
        posY = 0.64f,
        connections = listOf("13", "16", "4")
    ),
    DistrictConfig(
        id = "11",
        posX = 0.64f,
        posY = 0.64f,
        connections = listOf("5", "16", "14")
    ),
    DistrictConfig(
        id = "12",
        posX = 0.1f,
        posY = 0.64f,
        connections = listOf("6", "15", "17")
    ),
    DistrictConfig(
        id = "13",
        posX = 0.28f,
        posY = 0.64f,
        connections = listOf("7", "17", "10")
    ),
    DistrictConfig(
        id = "14",
        posX = 0.82f,
        posY = 0.64f,
        connections = listOf("8", "11", "18")
    ),
    DistrictConfig(
        id = "15",
        posX = 1.0f,
        posY = 0.64f,
        connections = listOf("12", "9", "18")
    ),
    DistrictConfig(
        id = "16",
        posX = 0.64f,
        posY = 0.82f,
        connections = listOf("11", "10", "19")
    ),
    DistrictConfig(
        id = "17",
        posX = 0.28f,
        posY = 0.82f,
        connections = listOf("12", "6", "17")
    ),
    DistrictConfig(
        id = "18",
        posX = 1.0f,
        posY = 0.82f,
        connections = listOf("1", "2", "3")
    ),
    DistrictConfig(
        id = "19",
        posX = 0.64f,
        posY = 1.0f,
        connections = listOf("17", "16", "18")
    ),
)