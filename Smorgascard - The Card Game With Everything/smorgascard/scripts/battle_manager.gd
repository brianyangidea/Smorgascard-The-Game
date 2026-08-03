#Manages battle logic

extends Node

const SMALL_CARD_SCALE = 0.8
const CARD_MOVE_SPEED = 0.2
const STARTING_HEALTH = 10
const BATTLE_POS_OFFSET = 25

var battle_timer
var empty_monster_card_slots = []
var opponent_cards_on_battlefield = []
var player_cards_on_battlefield = []
var player_health
var opponent_health


func _ready() -> void:
	battle_timer = $"../battle_timer"
	battle_timer.one_shot = true
	battle_timer.wait_time = 0.5
	
	empty_monster_card_slots.append($"../card_slots/enemy_card_slot1")
	empty_monster_card_slots.append($"../card_slots/enemy_card_slot2")
	empty_monster_card_slots.append($"../card_slots/enemy_card_slot3")
	empty_monster_card_slots.append($"../card_slots/enemy_card_slot4")
	empty_monster_card_slots.append($"../card_slots/enemy_card_slot5")
	
	player_health = STARTING_HEALTH
	$"../player_health".text = str(player_health)
	opponent_health = STARTING_HEALTH
	$"../enemy_health".text = str(opponent_health)

func _on_end_turn_button_pressed() -> void:
	opponents_turn()


func opponents_turn():
	$"../end_turn_button".disabled = true
	$"../end_turn_button".visible = false

	#Wait 0.5 second to avoid instant draw
	await wait(0.5)

	#Check for size. Only draw if cards exist.
	if $"../enemy_deck".enemy_deck.size() != 0:
		$"../enemy_deck".draw_card()
		#Wait 0.5 second to avoid instant play
		await wait(0.5)
	
	#Check if there's free minion card slots. If no, end turn
	if empty_monster_card_slots.size() != 0:
		await try_play_card()
		
	#try to attack
	if opponent_cards_on_battlefield.size() != 0:
		var enemy_cards_to_attack = opponent_cards_on_battlefield.duplicate()
		for card in enemy_cards_to_attack:
			if player_cards_on_battlefield.size() != 0:
				var card_to_attack = player_cards_on_battlefield.pick_random()
				await attack(card, card_to_attack, "opponent")
			else:
				await direct_attack(card, "opponent")
	
	#Ends turn
	end_opponent_turn()


#This function is for attacking someone directly
func direct_attack(attacking_card, attacker):
	var new_pos_y
	if attacker == "opponent":
		new_pos_y = 1080
	else:
		new_pos_y = 0
	var new_pos = Vector2(attacking_card.position.x, new_pos_y)
	
	attacking_card.z_index = 5
	
	#animate the attack
	var tween = get_tree().create_tween()
	tween.tween_property(attacking_card, "position", new_pos, CARD_MOVE_SPEED)
	await wait(0.2)
	
	if attacker == "opponent":
		#Deal damage to player
		player_health = max(0, player_health - attacking_card.attack)
		$"../player_health".text = str(player_health)
	else:
		#Deal damage to opponent
		opponent_health = max(0, opponent_health - attacking_card.attack)
		$"../enemy_health".text = str(opponent_health)
	
	#animate card back
	var tween2 = get_tree().create_tween()
	tween2.tween_property(attacking_card, "position", attacking_card.card_slot_card_is_in.position, CARD_MOVE_SPEED)
	
	attacking_card.z_index = 0
	await wait(1.0)


#This function is for a card attacking another card (card on card violence!)
func attack(attacking_card, defending_card, attacker):
	attacking_card.z_index = 5
	var new_pos = Vector2(defending_card.position.x, defending_card.position.y + BATTLE_POS_OFFSET)
	
	#animate the attack
	var tween = get_tree().create_tween()
	tween.tween_property(attacking_card, "position", new_pos, CARD_MOVE_SPEED)
	await wait(0.2)
	var tween2 = get_tree().create_tween()
	tween2.tween_property(attacking_card, "position", attacking_card.card_slot_card_is_in.position, CARD_MOVE_SPEED)
	
	#Cards doing damage to each other
	defending_card.health = max(0, defending_card.health - attacking_card.attack)
	defending_card.get_node("health").text = str(defending_card.health)
	attacking_card.health = max(0, attacking_card.health - defending_card.attack)
	attacking_card.get_node("health").text = str(attacking_card.health)
	
	attacking_card.z_index = 0
	await wait(0.2)
	
	var card_was_destroyed = false
	#Destroys a card if health reaches 0
	if attacking_card.health == 0:
		destroy_card(attacking_card, attacker)
		card_was_destroyed = true
	if defending_card.health == 0:
		if attacker == "player":
			destroy_card(defending_card, "opponent")
		else:
			destroy_card(defending_card, "player")
		card_was_destroyed = true
		
	if card_was_destroyed:
		await wait(1.0)


#Move card to discard pile
#Remove card from any arrays or stuff it may be in (ex. player_cards_on_battlefield)
func destroy_card(card, card_owner):
	var new_pos
	if card_owner == "player":
		new_pos = $"../player_discard".position
	else:
		new_pos = $"../opponent_discard".position
		
	#animate
	var tween = get_tree().create_tween()
	tween.tween_property(card, "position", new_pos, CARD_MOVE_SPEED)
	await wait(0.15)
	


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
	var random_enemy_monster_card_slot = empty_monster_card_slots.pick_random()
	empty_monster_card_slots.erase(random_enemy_monster_card_slot)
	
	#loop through to get card with highest attack
	var card_with_highest_attack = enemy_hand[0]
	for card in enemy_hand:
		if card.attack > card_with_highest_attack.attack:
			card_with_highest_attack = card
			
	#Remove the played card from the opponent's hand
	$"../enemy_hand".remove_card_from_hand(card_with_highest_attack)
	card_with_highest_attack.card_slot_card_is_in = random_enemy_monster_card_slot
	
	opponent_cards_on_battlefield.append(card_with_highest_attack)
		
	#Card animation code:
	#animate card into position
	var tween = get_tree().create_tween()
	tween.tween_property(card_with_highest_attack, "position", random_enemy_monster_card_slot.position, CARD_MOVE_SPEED)
	var tween2 = get_tree().create_tween()
	tween2.tween_property(card_with_highest_attack, "scale", Vector2(SMALL_CARD_SCALE, SMALL_CARD_SCALE), CARD_MOVE_SPEED)
	card_with_highest_attack.get_node("AnimationPlayer").play("card_flip")
	
	await wait(1.0)


#Simple function to wait for a bit. Note: Must be used with the "await" keyword in code.
func wait(wait_time):
	battle_timer.wait_time = wait_time
	battle_timer.start()
	await battle_timer.timeout


#End turn:
#Reset player deck draw
func end_opponent_turn():
	$"../deck".reset_draw()
	$"../end_turn_button".disabled = false
	$"../end_turn_button".visible = true
