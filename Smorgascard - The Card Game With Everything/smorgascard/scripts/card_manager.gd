extends Node2D

const COLLISION_MASK_CARD = 1

var card_being_dragged
var screen_size
var is_hovering_on_card


func _ready() -> void:
	screen_size = get_viewport_rect().size


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(_delta: float) -> void:
	if card_being_dragged:
		var mouse_pos = get_global_mouse_position()
		card_being_dragged.position = Vector2(clamp(mouse_pos.x, 0, screen_size.x), 
			clamp(mouse_pos.y, 0, screen_size.y))


func _input(event):
	if event is InputEventMouseButton and event.button_index == MOUSE_BUTTON_LEFT:
		if event.pressed:
			var card = check_for_card()
			if card:
				start_drag(card)
		else:
			finish_drag()
			

func start_drag(card):
	card_being_dragged = card
	card.scale = Vector2(0.8, 0.8)
	
	
func finish_drag():
	card_being_dragged.scale = Vector2(1.05, 1.05)
	card_being_dragged = null
	

#Connects the card_hovvered_over and card_hovvered_off functions to each card
func connect_card_signals(card):
	card.connect("hovered", card_hovvered_over)
	card.connect("hovered_off", card_hovvered_off)

#Called when card is hovered over
func card_hovvered_over(card):
	if !is_hovering_on_card:
		is_hovering_on_card = true
		highlight_card(card, true)
	
#Called when card is hovered off
func card_hovvered_off(card):
	if !card_being_dragged:
		highlight_card(card, false)
		#Check if we hovered straight from one card to another
		var new_card_hovered = check_for_card()
		if new_card_hovered and new_card_hovered != card:
			highlight_card(new_card_hovered, true)
		else:
			is_hovering_on_card = false
	
#Handles the highlighting of each card
func highlight_card(card, hovered_status):
	if hovered_status:
		card.scale = Vector2(1.05, 1.05)
		card.z_index = 2
	else:
		card.scale = Vector2(1, 1)
		card.z_index = 1

#This function checks where the mouse is clicking.
#It returns the card being interacted with if it is over a card
#and NULL otherwise
func check_for_card():
	var space_state = get_viewport().world_2d.direct_space_state
	var parameters = PhysicsPointQueryParameters2D.new()
	parameters.position = get_viewport().get_mouse_position()
	parameters.collide_with_areas = true
	parameters.collision_mask = COLLISION_MASK_CARD
	var result = space_state.intersect_point(parameters)
	if result.size() > 0:
		return get_card_with_highest_z_index(result)
	return null
	
func get_card_with_highest_z_index(cards):
	#Assume first card has highest z index
	var highest_z_card = cards[0].collider.get_parent()
	var highest_z_index = highest_z_card.z_index
	
	#Loop through the rest of the cards to look for the highest z index (aka layer)
	for i in range(1, cards.size()):
		var current_card = cards[i].collider.get_parent()
		if current_card.z_index > highest_z_index:
			highest_z_card = current_card
			highest_z_index = current_card.z_index
	return highest_z_card
