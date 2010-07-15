package models;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.parsit.IFootNote;
import org.parsit.IFootNoteFactory;

import play.data.validation.MaxSize;
import play.db.jpa.Model;

@Entity
public class FootNote extends Model implements IFootNote {
	
	/** Footnote HTML content */
	@MaxSize(1000)
	@Lob
	public String content;
	
	/** Parent post where this footnote is referenced */
	@ManyToOne
	public Paragraph paragraph;
	
	
	public FootNote() {
		
	}
	
	@Override
	public String getIdentifier() {
		return id.toString() + "_";
	}
	
	@Override
	public String getContent() {
		return content;
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}
	
	
	public static class Factory implements IFootNoteFactory {
		@Override
		public IFootNote createFootNote() {
			FootNote footNote = new FootNote();
			footNote.save();
			return footNote;
		}
	}
}
