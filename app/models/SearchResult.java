package models;

/**
 * This class initializes all the attributes pertaining to Reddit
 */
public class SearchResult {
  public String title;
  public String author;
  public String selftext;
  public String subreddit;
  public String author_fullname;
  public String author_is_blocked;
  public String author_premium;

  public SearchResult() {}

  /**
   * SearchResult public default constructor. I
   * @author Saghana Mahesh Sarma
   * @param title - Title of the post
   * @param author - Author of the post
   * @param selftext - Description of the post
   * @param subreddit - Subreddit of the post
   * @param author_fullname - Full name of the author
   * @param author_is_blocked - Post author is blocked
   * @param author_premium - Post author is premium
   */
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
