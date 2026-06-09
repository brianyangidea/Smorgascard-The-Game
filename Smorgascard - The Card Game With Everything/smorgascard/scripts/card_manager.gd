extends Node2D

const COLLISION_MASK_CARD = 1
const COLLISION_MASK_CARD_SLOT = 2
const DEFAULT_CARD_MOVE_SPEED = 0.1

var card_being_dragged
var screen_size
var is_hovering_on_card
var player_hand_reference


#Called when the node enters the scene tree for the first time.
func _ready() -> void:
	screen_size = get_viewport_rect().size
	player_hand_reference = $"../player_hand"
	$"../input_manager".connect("left_mouse_button_released", on_left_click_released)

#Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(_delta: float) -> void:
	if card_being_dragged:
		var mouse_pos = get_global_mouse_position()
		card_being_dragged.position = Vector2(clamp(mouse_pos.x, 0, screen_size.x), 
			clamp(mouse_pos.y, 0, screen_size.y))


#Simple fuction that scales down the card when it is being dragged
func start_drag(card):
	card_being_dragged = card
	card.scale = Vector2(0.8, 0.8)
	

#Ran once the card is done being dragged. Checks for card slots.
func finish_drag():
	card_being_dragged.scale = Vector2(1.05, 1.05)
	var card_slot_found = check_for_card_slot()
	if card_slot_found and !card_slot_found.card_in_slot:
		player_hand_reference.remove_card_from_hand(card_being_dragged)
		#Card dropped into slot
		card_being_dragged.position = card_slot_found.position
		#The two lines below prevents interaction with cards alrady in a slot
		card_being_dragged.get_node("Area2D/CollisionShape2D").disabled = true
		card_slot_found.card_in_slot = true
	else:
		player_hand_reference.add_card_to_hand(card_being_dragged, DEFAULT_CARD_MOVE_SPEED)
	card_being_dragged = null
	

#Connects the card_hovered_over and card_hovered_off functions to each card
func connect_card_signals(card):
	card.connect("hovered", card_hovered_over)
	card.connect("hovered_off", card_hovered_off)


func on_left_click_released():
	if card_being_dragged:
		finish_drag()

#Called when card is hovered over. Calls highlight_card to turn on.
func card_hovered_over(card):
	if !is_hovering_on_card:
		is_hovering_on_card = true
		highlight_card(card, true)
	
	
#Called when card is hovered off. Calls highlight_card to turn off.
#Also has check to make sure we only highlight the correct card
func card_hovered_off(card):
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
	

#This function checks for the card slot
func check_for_card_slot():
	var space_state = get_viewport().world_2d.direct_space_state
	var parameters = PhysicsPointQueryParameters2D.new()
	parameters.position = get_viewport().get_mouse_position()
	parameters.collide_with_areas = true
	parameters.collision_mask = COLLISION_MASK_CARD_SLOT
	var result = space_state.intersect_point(parameters)
	if result.size() > 0:
		return result[0].collider.get_parent()
	return null
	

#This function is used in tandem with dragging. It's to ensure we
#only drag the card that's actually visually on top of the other cards.
#(Z-indices are basically the layers.)
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
