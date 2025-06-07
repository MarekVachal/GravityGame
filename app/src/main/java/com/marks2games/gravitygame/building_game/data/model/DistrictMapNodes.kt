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
        posY = 0.45f,
        connections = listOf("1", "2", "3")
    ),
    DistrictConfig(
        id = "1",
        posX = 0.3f,
        posY = 1.0f,
        connections = listOf("0", "4", "5")
    ),
    DistrictConfig(
        id = "2",
        posX = 0.3f,
        posY = 0.45f,
        connections = listOf("0", "6", "4")
    ),
    DistrictConfig(
        id = "3",
        posX = 0.3f,
        posY = 0.1F,
        connections = listOf("0", "6", "5")
    ),
    DistrictConfig(
        id = "4",
        posX = 0.6f,
        posY = 1.0f,
        connections = listOf("1", "7", "2")
    ),
    DistrictConfig(
        id = "5",
        posX = 0.6f,
        posY = 0.1f,
        connections = listOf("1", "3", "7")
    ),
    DistrictConfig(
        id = "6",
        posX = 0.6f,
        posY = 0.45f,
        connections = listOf("2", "7", "3")
    ),
    DistrictConfig(
        id = "7",
        posX = 1.0f,
        posY = 0.45f,
        connections = listOf("6", "5", "4")
    )
)

val mediumPlanetDistrictNodes = listOf(
    DistrictConfig(
        id = "0",
        posX = 0.1f,
        posY = 0.45f,
        connections = listOf("1", "2", "3")
    )
)

val largePlanetDistrictNodes = listOf(
    DistrictConfig(
        id = "0",
        posX = 0.0f,
        posY = 0.0f,
        connections = listOf("1", "2", "3")
    ),
    DistrictConfig(
        id = "1",
        posX = 0.0f,
        posY = 0.0f,
        connections = listOf("1", "2", "3")
    ),
    DistrictConfig(
        id = "2",
        posX = 0.0f,
        posY = 0.0f,
        connections = listOf("1", "2", "3")
    ),
    DistrictConfig(
        id = "3",
        posX = 0.0f,
        posY = 0.0f,
        connections = listOf("1", "2", "3")
    ),
    DistrictConfig(
        id = "4",
        posX = 0.0f,
        posY = 0.0f,
        connections = listOf("1", "2", "3")
    ),
    DistrictConfig(
        id = "5",
        posX = 0.0f,
        posY = 0.0f,
        connections = listOf("1", "2", "3")
    ),
    DistrictConfig(
        id = "6",
        posX = 0.0f,
        posY = 0.0f,
        connections = listOf("1", "2", "3")
    ),
    DistrictConfig(
        id = "7",
        posX = 0.0f,
        posY = 0.0f,
        connections = listOf("1", "2", "3")
    ),
    DistrictConfig(
        id = "8",
        posX = 0.0f,
        posY = 0.0f,
        connections = listOf("1", "2", "3")
    ),
    DistrictConfig(
        id = "9",
        posX = 0.0f,
        posY = 0.0f,
        connections = listOf("1", "2", "3")
    ),
    DistrictConfig(
        id = "10",
        posX = 0.0f,
        posY = 0.0f,
        connections = listOf("1", "2", "3")
    ),
    DistrictConfig(
        id = "11",
        posX = 0.0f,
        posY = 0.0f,
        connections = listOf("1", "2", "3")
    ),
    DistrictConfig(
        id = "12",
        posX = 0.0f,
        posY = 0.0f,
        connections = listOf("1", "2", "3")
    ),
    DistrictConfig(
        id = "13",
        posX = 0.0f,
        posY = 0.0f,
        connections = listOf("1", "2", "3")
    ),
    DistrictConfig(
        id = "14",
        posX = 0.0f,
        posY = 0.0f,
        connections = listOf("1", "2", "3")
    ),
    DistrictConfig(
        id = "15",
        posX = 0.0f,
        posY = 0.0f,
        connections = listOf("1", "2", "3")
    ),
    DistrictConfig(
        id = "16",
        posX = 0.0f,
        posY = 0.0f,
        connections = listOf("1", "2", "3")
    ),
    DistrictConfig(
        id = "17",
        posX = 0.0f,
        posY = 0.0f,
        connections = listOf("1", "2", "3")
    ),
    DistrictConfig(
        id = "18",
        posX = 0.0f,
        posY = 0.0f,
        connections = listOf("1", "2", "3")
    ),
    DistrictConfig(
        id = "19",
        posX = 0.0f,
        posY = 0.0f,
        connections = listOf("1", "2", "3")
    ),
)