#Basic script that keeps track of the deck

extends Node2D

const CARD_SCENE_PATH = "res://scenes/card.tscn"
const CARD_DRAW_SPEED = 0.2 #Controls speed at which cards are drawn out of deck
const STARTING_HAND_SIZE = 5

#This variable contains the player's entire deck and all its cards
var player_deck = ["Knight", "Archer", "Demon", "Knight", "Tornado", "Tornado", "Tornado"]
var card_database_reference 
var drawn_card_this_turn = false


# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	player_deck.shuffle()
	$RichTextLabel.text = str(player_deck.size())
	card_database_reference = preload("res://scripts/card_database.gd")

	for i in range(STARTING_HAND_SIZE):
		draw_card()
		drawn_card_this_turn = false
	drawn_card_this_turn = true

# Everytime a card is drawn, it must be "instantiated" aka turned into an actual card with its attributes
# This function does that. It's a very important fuction!
func draw_card():
	if drawn_card_this_turn:
		return
	drawn_card_this_turn = true
	
	var card_drawn_name = player_deck[0]
	player_deck.erase(card_drawn_name)
	
	#Check if drawn card was last card in deck
	#Disables deck and it's visibility if true
	if player_deck.size() == 0:
		$Area2D/CollisionShape2D.disabled = true
		$Sprite2D.visible = false
		$RichTextLabel.visible = false
	
	#This is where we instantiate the card	
	$RichTextLabel.text = str(player_deck.size())
	var card_scene = preload(CARD_SCENE_PATH)
	var new_card = card_scene.instantiate()
	var card_image_path = str("res://assets/" + card_drawn_name + "_card.png")
	
	new_card.position = self.position
	new_card.get_node("card_image").texture = load(card_image_path)
	
	#Checks if the card is a monster or a magic card and sets it up accordingly
	new_card.card_type = card_database_reference.CARDS[card_drawn_name][2]
	if new_card.card_type == "monster":
		new_card.get_node("ability").visible = false
		new_card.attack = card_database_reference.CARDS[card_drawn_name][0]
		new_card.get_node("attack").text = str(new_card.attack)
		new_card.health = card_database_reference.CARDS[card_drawn_name][1]
		new_card.get_node("health").text = str(new_card.health)
	elif new_card.card_type == "magic":
		new_card.get_node("attack").visible = false
		new_card.get_node("health").visible = false
		new_card.get_node("ability").text = card_database_reference.CARDS[card_drawn_name][3]
		var new_card_ability_script_path = card_database_reference.CARDS[card_drawn_name][4]
		if new_card_ability_script_path:
			new_card.ability_script = load(new_card_ability_script_path).new()
	
	$"../card_manager".add_child(new_card)
	new_card.name = "Card"
	$"../player_hand".add_card_to_hand(new_card, CARD_DRAW_SPEED)
	new_card.get_node("AnimationPlayer").play("card_flip")


func reset_draw():
	drawn_card_this_turn = false
