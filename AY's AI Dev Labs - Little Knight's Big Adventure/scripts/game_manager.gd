extends Node

var score = 0

@onready var coin_count: Label = $CoinCount

func add_point():
	score += 1
	coin_count.text = "You collected " + str(score) + " coins!"
