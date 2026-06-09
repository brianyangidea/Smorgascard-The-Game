extends Node2D

const CARD_SCENE_PATH = "res://scenes/card.tscn"
const CARD_DRAW_SPEED = 0.2 #Controls speed at which cards are drawn out of deck

#This variable contains the player's entire deck and all its cards
var player_deck = ["knight", "knight", "knight", "knight"]

# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	$RichTextLabel.text = str(player_deck.size())


# Called every frame. 'delta' is the elapsed time since the previous frame.
func draw_card():
	var card_drawn = player_deck[0]
	player_deck.erase(card_drawn)
	
	#Check if drawn card was last card in deck
	#Disables deck and it's visibility if true
	if player_deck.size() == 0:
		$Area2D/CollisionShape2D.disabled = true
		$Sprite2D.visible = false
		$RichTextLabel.visible = false
		
	$RichTextLabel.text = str(player_deck.size())
	var card_scene = preload(CARD_SCENE_PATH)
	var new_card = card_scene.instantiate()
	new_card.position = self.position
	$"../card_manager".add_child(new_card)
	new_card.name = "Card"
	$"../player_hand".add_card_to_hand(new_card, CARD_DRAW_SPEED)
