package models;

public class SearchResult {
  public String title;
  public String author;
  public String selftext;
  public String subreddit;
  public String author_fullname;
  public String author_is_blocked;
  public String author_premium;

  public SearchResult() {}
  public SearchResult(String title, String author, String selftext, String subreddit, String author_fullname, String author_is_blocked, String author_premium) {
    this.title = title;
    this.author = author;
    this.selftext = selftext;
    this.subreddit = subreddit;
    this.author_fullname = author_fullname;
    this.author_is_blocked = author_is_blocked;
    this.author_premium = author_premium;
  }
}
