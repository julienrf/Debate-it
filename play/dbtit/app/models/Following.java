package models;

import siena.Generator;
import siena.Id;
import siena.Model;
import siena.NotNull;

public class Following extends Model {

	@Id(Generator.AUTO_INCREMENT)
	public Long id;
	
	@NotNull
	public User user;
	
	@NotNull
	public Thread thread;
}
