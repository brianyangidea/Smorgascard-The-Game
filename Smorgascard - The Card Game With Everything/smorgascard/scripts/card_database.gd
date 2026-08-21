#A large database that outlines the health, attack, and other properties of each card

const CARDS = { #[Attack, Health, Card Type, Ability Text, Ability Script Path]
	# Minions:
	"Knight" : [2, 3, "monster", null, null],
	"Archer" : [1, 1, "monster", null, null],
	"Demon" : [5, 7, "monster", null, null],
	
	# Magic:
	"Tornado" : [null, null, "magic", "Deal 1 Damage To All Enemy Minions", "res://scripts/abilities/Tornado.gd"]
}
