package models;

import java.util.List;
import java.util.Map;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Tag extends Model implements Comparable<Tag> {

	public String name;
	
	private Tag(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public static Tag findOrCreateByName(String name) {
		Tag tag = Tag.find("byName", name).first();
		if (tag == null) {
			tag = new Tag(name);
		}
		return tag;
	}
	
	public static List<Map<String, String>> cloud() {
		return Tag.find("SELECT NEW map(tag.name AS tag, COUNT(t.id) AS weight) FROM Thread t JOIN t.tags AS tag GROUP BY tag.name ORDER BY tag.name").fetch();
	}

	@Override
	public int compareTo(Tag o) {
		return this.name.compareTo(o.name);
	}
}
