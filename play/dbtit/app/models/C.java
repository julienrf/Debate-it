package models;

import siena.Generator;
import siena.Id;
import siena.Model;
import siena.Query;

public class C extends Model {
	
	@Id
	public Long id;
	
	public static Query<C> all() {
		return Model.all(C.class);
	}
}
