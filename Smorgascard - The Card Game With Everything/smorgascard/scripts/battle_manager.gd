#Manages battle logic

extends Node

const SMALL_CARD_SCALE = 0.8
const CARD_MOVE_SPEED = 0.2

var battle_timer
var empty_monster_card_slots = []


func _ready() -> void:
	battle_timer = $"../battle_timer"
	battle_timer.one_shot = true
	battle_timer.wait_time = 0.5
	
	empty_monster_card_slots.append($"../card_slots/enemy_card_slot1")
	empty_monster_card_slots.append($"../card_slots/enemy_card_slot2")
	empty_monster_card_slots.append($"../card_slots/enemy_card_slot3")
	empty_monster_card_slots.append($"../card_slots/enemy_card_slot4")
	empty_monster_card_slots.append($"../card_slots/enemy_card_slot5")
	

func _on_end_turn_button_pressed() -> void:
	opponents_turn()


func opponents_turn():
	$"../end_turn_button".disabled = true
	$"../end_turn_button".visible = false

	#Wait 0.5 second to avoid instant draw
	battle_timer.start()
	await battle_timer.timeout

	#Check for size. Only draw if cards exist.
	if $"../enemy_deck".enemy_deck.size() != 0:
		$"../enemy_deck".draw_card()
		#Wait 0.5 second to avoid instant play
		battle_timer.start()
		await battle_timer.timeout
	
	#Check if there's free minion card slots. If no, end turn
	if empty_monster_card_slots.size() == 0:
		end_opponent_turn()
		return
	
	#Play card:
	try_play_card()
	
	#Ends turn
	end_opponent_turn()
	

#This function contain's the oppoent's AI for card playing
#as well as a few animation bits
func try_play_card():
	#Opponent's basic AI:
	#For now, it just plays the card with the highest attack in a random empty slot
	var enemy_hand = $"../enemy_hand".enemy_hand
	if enemy_hand.size() == 0:
		end_opponent_turn()
		return
	#Get random empty slot
	var random_enemy_monster_card_slot = empty_monster_card_slots[randi_range(0, empty_monster_card_slots.size() - 1)]
	empty_monster_card_slots.erase(random_enemy_monster_card_slot)
	#loop through to get card with highest attack
	var card_with_highest_attack = enemy_hand[0]
	for card in enemy_hand:
		if card.attack > card_with_highest_attack.attack:
			card_with_highest_attack = card
			
	#Remove the played card from the opponent's hand
	$"../enemy_hand".remove_card_from_hand(card_with_highest_attack)
		
	#Card animation code:
	#animate card into position
	var tween = get_tree().create_tween()
	tween.tween_property(card_with_highest_attack, "position", random_enemy_monster_card_slot.position, CARD_MOVE_SPEED)
	var tween2 = get_tree().create_tween()
	tween2.tween_property(card_with_highest_attack, "scale", Vector2(SMALL_CARD_SCALE, SMALL_CARD_SCALE), CARD_MOVE_SPEED)
	card_with_highest_attack.get_node("AnimationPlayer").play("card_flip")


#End turn:
#Reset player deck draw
func end_opponent_turn():
	$"../deck".reset_draw()
	$"../end_turn_button".disabled = false
	$"../end_turn_button".visible = true
