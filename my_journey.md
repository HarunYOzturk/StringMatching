### My Journey and My Research (Part 2 and Part 4) ###
Before starting this homework, I did not have a clear idea of what the Boyer Moore algorithm is and how and why it works. 
I have not been able to grasp it properly. 
But since this homework required us to implement it on our own, I had to fully understand it before I could write code about it.
So at first, I went to YouTube and watched some videos to understand how the Boyer Moore algorithm works. 
Then after watching these videos, I had some questions. So to clear up those questions, I used ChatGPT. 
One of my main questions was “Why does this algorithm work?”.
Because it is easier to understand how this algorithm works, but the why part is the most difficult.
After clearing up my concepts, I tried to implement it in Java on my own. I wrote code for it. 
But when I ran it, I got an StringIndexOutOfBounds exception. I looked at my code and tried to fix the issue. 
I was able to fix it but then I got another error. After a good amount of time, I was able to make my BoyerMoore algorithm work properly.
Then I ran it on the test cases provided. 
Most of the test cases passed, except for the ones where we have repeating patterns like finding “AAA” in “AAAAAAA”. My algorithm was failing in such test cases. 
I looked at my algorithm again to figure out what I need to fix in order to solve this problem. 
I realized that when a pattern was found, the index was moving forward by the number of characters in the pattern. 
Instead, it had to move by just one character to solve such test cases. I fixed it and it was able to pass all test cases.
I did learn Boyer Moore in detail but it was challenging at the start.
The analysis part was not that difficult. I did take some help from ChatGPT in order to improve the function that checks if we have repeating patterns or not. 
I also took an idea about how many characters make the text or a pattern long. 
The function that is used to check if we have repeating patterns is inspired by the LPS table in the KMP algorithm.
But mainly it was my original effort as I can reproduce any code that I have written. I did use ChatGPT for ideas and help.

### REPORT ###
The Boyer Moore algorithm that I have implemented is that standard Boyer Moore algorithm that is used in the industry with a few changes. 
Firstly, we have used an index variable to keep track of where we are in the main text and whenever we find a match, we just move forward by one space in the entire text. 
Secondly, in the standard implementation, an integer array of length 256 is used because there are 256 ASCII characters. 
But I have used a hashmap instead. This has allowed me to consider any character (the Unicode ones as well).
I did not implement the GoCrazy algorithm
For the Preanalysis part, I wrote a function that counts how many alphabets are there in a string. 
I also wrote a function that checks if a string is repetitive or not. For the very basic cases where the pattern is very small or empty or longer than the text, we have used the Naive algorithm. 
For patterns that are repetitive, we have used the KMP algorithm. When the text and the pattern are both very long, we have used the Rabin Kalp algorithm since it uses a hash function. 
It is ideal because the pattern and text are both long and checking each character one by one is not a very feasible approach and is only preferable if we know that a part in the text could be a potential match with the pattern. 
When there are many alphabets in the text and the pattern itself is also long, it is ideal to use the Boyer Moore algorithm. 
It is also feasible to use the Boyer Moore algorithm when the alphabet size of the text is small but the text is long. 
For all other cases, we have relied on the Naive string matching algorithm.
The Analysis was very accurate since our pre-analysis approach was able to find the optimal algorithm for most of the times.



CHAT LINKS
https://chatgpt.com/share/693f113a-3640-800f-af72-587937c812a6
https://chatgpt.com/share/693f1198-c0c8-800f-8fc2-70b5372a2290
https://chatgpt.com/share/693f11e6-9630-800f-b4c5-4d3f0ebb3be1
