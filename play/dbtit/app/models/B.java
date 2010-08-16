package models;

import siena.Id;
import siena.Index;
import siena.Model;
import siena.Query;

public class B extends Model {
	
	@Id
	public Long id;
	
	@Index("a_index")
	public A a;
	
	@Index("c_index")
	public C c;
	
	public B(A a, C c) {
		this.a = a;
		this.c = c;
	}
	
	public static Query<B> all() {
		return Model.all(B.class);
	}
}
