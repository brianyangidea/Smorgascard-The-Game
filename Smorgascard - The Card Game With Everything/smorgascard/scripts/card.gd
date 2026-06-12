#This script handles the basic card visuals
#Also ensure all cards are a CHILD of card_manager or else ERRORS GALORE

extends Node2D

signal hovered
signal hovered_off

var hand_position
var card_slot_card_is_in
var card_type


#Called when the node enters the scene tree for the first time.
func _ready() -> void:
	#All cards must be a CHILD of card_manager or else ERRORS GALORE
	get_parent().connect_card_signals(self)


#Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(_delta: float) -> void:
	pass


#Called when the mouse enters a card hitbox
func _on_area_2d_mouse_entered() -> void:
	emit_signal("hovered", self)


#Called when the mouse exits a card hitbox
func _on_area_2d_mouse_exited() -> void:
	emit_signal("hovered_off", self)
