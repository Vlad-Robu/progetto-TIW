package tiw.beans;

import java.sql.Date;
import java.time.LocalDate;

public class AppelloBean{
	private int id;
	private int courseId;
	private String courseName;
	private LocalDate date;

	public void setId(int id) { this.id = id; }
	public int getId() { return this.id; }
	
	public void setCourseId(int courseId) { this.courseId = courseId; }
	public int getCourseId() { return this.courseId; }
	
	public void setDate(Date date) { this.date = date.toLocalDate(); }
	
	public LocalDate getDate() { return this.date; }
	
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
}
