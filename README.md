# Overview

As a software engineer, I'm always looking to build my skills and learn coding. By working on various projects using languages I'm unfamiliar with, I solidify my foundations in coding and create tangible results as proof of my learning.

I planned to create an expense tracker, a Java-based desktop application that records daily expenses with categories (such as food, entertainment, travel, etc.), has a viewable list of all recorded expenses, calculates and displays total expenses, and then calculates and displays by category. Managing daily expenses can be a cumbersome task. The purpose of this project is to simplify the process by providing an intuitive and easy-to-use application. In the end, I achieved this, adding an additional graphical user interface for enhanced usability.

The primary motivation behind this software was to dive deeper into Java, a language I've been interested in for a while. It also served as a test of my coding foundations, allowing me to explore various language features and libraries while solving a real-world problem: simplifying expense tracking.

[Software Demo Video](http://youtube.link.goes.here)

# Development Environment

I used Visual Studio Code with the complete Java Extension Pack. I did contemplate using a more industry standard code editor such as Eclipse, but once I got the Java Extension Pack working for VSC, I realized that it's everything I needed. The Play button as well as live error-checking are features I can no longer live without. I also had extensive help from ChatGPT and the Internet.

I developed with JDK version "21" 2023-09-19 LTS. From the Java Utilities libraries, I used ArrayList, HashMap, HashSet, Map, Set, and Iterator. The ArrayList held all expenses and proved to be a good data structure for storing in a file. HashMap was useful for converting category code into `String`s and vice versa. HashSet was used for a one-time operation of checking if the category filter was enabled before deleting expenses. The Iterator was also a one-time use to iterate through the `ArrayList` and ensure the right expenses were deleted. For the GUI, I used Abstract Window Toolkit and, especially, Swing. Swing is the backbone of the GUI, AWT was only there to help me structure each GUI component.

# Useful Websites

- [ChatGPT](https://chat.openai.com/) - Coding Assistant.
- [Oracle's Documentation](https://docs.oracle.com/en/java/javase/21/) - For explicit instructions on built-in classes and functions.
- [Stack Overflow](https://stackoverflow.com/) - Sometimes I forget how to make a loop.
- [Tabnine](https://www.tabnine.com/) - Also has documentation on some built-in classes.
- [java2s](http://www.java2s.com/) - More class and library documentation.
- [Guru99](https://www.guru99.com/java-swing-gui.html) - Primarily for help on using Swing.
- [Apache Netbeans](https://netbeans.apache.org/) - Built the foundation of my GUI design.
- [javaTpoint](https://www.javatpoint.com/) - I learned how to convert a `String` to an `int`.

# Future Work

- Separate monolithic code into proper classes.
- Comment every line.
- Quality of life improvements such as truncating the Total calculation.
- More aesthetically-pleasing GUI.