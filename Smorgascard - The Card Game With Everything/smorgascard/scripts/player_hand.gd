extends Node2D

const CARD_WIDTH = 130
const HAND_Y_POSITION = 900
const DEFAULT_CARD_MOVE_SPEED = 0.2

var player_hand = []
var center_screen_x


# Called when the node enters the scene tree for the first time.
func _ready() -> void:
	center_screen_x = get_viewport().size.x / 2
	

#Used with a for loop to add cards to a player's hand
func add_card_to_hand(card, speed):
	if card not in player_hand:
		player_hand.insert(0, card)
		update_hand_positions(speed)
	else:
		animate_card_to_position(card, card.hand_position, DEFAULT_CARD_MOVE_SPEED)


#Activate this function to check cards and update card positions in the hand.
#Handy after a card is played
func update_hand_positions(speed):
	for i in range(player_hand.size()):
		#get new card position based on index 
		var new_position = Vector2(calculate_card_position(i), HAND_Y_POSITION)
		var card = player_hand[i]
		card.hand_position = new_position
		animate_card_to_position(card, new_position, speed)
		
		
#Helper function to calculate the proper position of where to put the card in hand
func calculate_card_position(index):
	var total_width = (player_hand.size() - 1) * CARD_WIDTH
	var x_offset = center_screen_x + index * CARD_WIDTH - total_width / 2
	return x_offset


#Responsible for that swoop animation of the card.
#Change that number to change the speed of the animation
func animate_card_to_position(card, new_position, speed):
	var tween = get_tree().create_tween()
	tween.tween_property(card, "position", new_position, speed)
	
	
#Erases cards from the player hand after they are played or such
func remove_card_from_hand(card):
	if card in player_hand:
		player_hand.erase(card)
		update_hand_positions(DEFAULT_CARD_MOVE_SPEED)
