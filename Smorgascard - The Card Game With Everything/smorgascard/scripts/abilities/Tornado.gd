extends Node

const TORNADO_DAMAGE = 1


#Ability: Deal 1 damage to all enemy minions on the field
func trigger_ability(battle_manager_reference, card_with_ability, input_mananger_reference):

	#Disables end turn button & inputs for duration of animation
	$input_manager.inputs_disabled = true
	battle_manager_reference.enable_end_turn_button(false)
	
	await battle_manager_reference.wait(1.0)
	var cards_to_destroy = []
	
	########### THE ACTUAL ABILITY SECTION
	for card in battle_manager_reference.opponent_cards_on_battlefield:
		card.health = max(0, card.health - TORNADO_DAMAGE)
		card.get_node("health").text = str(card.health)
		if card.health == 0:
			cards_to_destroy.append(card)
	###########
	
	await battle_manager_reference.wait(1.0)
	
	#destroy killed cards
	if cards_to_destroy.size() > 0:
		for card in cards_to_destroy:
			battle_manager_reference.destroy_card(card, "opponent")
			
	#destroy played spell card once ability finishes
	battle_manager_reference.destroy_card(card_with_ability, "player")
	
	await battle_manager_reference.wait(1.0)
	
	#Enable end turn button & inputs
	$input_manager.inputs_disabled = false
	battle_manager_reference.enable_end_turn_button(false)
