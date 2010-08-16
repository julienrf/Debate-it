package models;

import siena.Generator;
import siena.Id;
import siena.Model;
import siena.Query;

public class A extends Model {
	
	@Id
	public Long id;
	
	public static Query<A> all() {
		return Model.all(A.class);
	}
	
	public void addC(C c) {
		B b = new B(this, c);
		b.insert();
		
		B b2 = B.all().filter("id", b.id).get();
		
		System.out.println(id + " " + b.a.id + " " + b2.a.id);
		System.out.println(c.id + " " + b.c.id + " " + b2.c.id);
	}
}
