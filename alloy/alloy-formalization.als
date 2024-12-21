sig Value {}

abstract sig Owner {
	owns: lone Value
}

sig ImmutableOwner extends Owner {
	immutablyBorrowsFrom: lone Owner
}

sig MutableOwner extends Owner {
	mutablyBorrowsFrom: lone MutableOwner
}


sig Environment {
	contains: set Owner
}

sig EnvStack {
	stack: set Environment
}

pred immutablyBorrows[o1: ImmutableOwner, o2: Owner]{
	o1.immutablyBorrowsFrom = o2
}

pred mutablyBorrows[o1: MutableOwner, o2: MutableOwner]{
	o1.mutablyBorrowsFrom = o2
}

pred owns[o: Owner, v: Value]{
	o.owns = v
}


fact {
	// all environments must be in the stack
	all e: Environment | some es: EnvStack | e in es.stack

	// all owners must be in an environment
	all o: Owner | some e: Environment | o in e.contains

	// all values must have at least one owner
	all v: Value | some o: Owner | owns[o, v]

	// a variable cannot borrow itself
	all io: ImmutableOwner | io.immutablyBorrowsFrom != io
	all mo: MutableOwner | mo.mutablyBorrowsFrom != mo

	// if a variable has mutable references in scope, 
	//there cannot be another reference to the same variable in the same scope
	some mo1, mo2: MutableOwner, e: Environment | all mo3: MutableOwner, io: ImmutableOwner 
		| ((mo1 + mo2 + mo3 + io) in e.contains 
		&& mutablyBorrows[mo1, mo2]) => !(immutablyBorrows[io, mo2] || mutablyBorrows[mo3, mo2])

	// if a variable owns a value, it cannot also borrow another
	all io: ImmutableOwner | some io.owns => no io.immutablyBorrowsFrom
	all mo: MutableOwner | some mo.owns => no mo.mutablyBorrowsFrom
}

pred ShowSomeInstances {
	(some io: ImmutableOwner, mo: MutableOwner |  some io.immutablyBorrowsFrom && some mo.mutablyBorrowsFrom) && #Value<= 2 && #EnvStack=1
 }

run ShowSomeInstances for 4
