# My Journey
I will first explain how I made my researchs for the tasks and made the implementations. 
Then I will talk about my challanges and experiences.

Formal report is on the root directory, named as Report.pdf.

## Research And Implementation
### Task 1: Boyer Moore Implementation
My Boyer Moore implementation is most likely familiar with the implementation on the **website_url_1**.

There is one difference though. We always assumed all string matchings are done with ASCII characters.
However there were one test case that a character that is not on the ASCII table. So, I added
a loop to find the maximum character value in text and pattern to create the proper table. This loop
added an extra complexity, but made the algorithm more reliable. 

### Task 2: GoCrazy
I couldn't.

### Task 3: PreAnalysis
I got help from GPT to do this. The versions of I made had terrible performance on tests, so I used GPT's
solution. It suggested an interesting way that deciding by calculating entropy of a text. Documentation exists 
above the StudentPreAnalysis class. Also, my chat link is on **website_url_2**.

## Challanges And Experiences
Since the beginning of this semester, I feel like I've lost my passion to this department. Especially in the
algorithms lecture, I cannot understand the topics. Even if I do, the implementation of the topic that I assumed I understood is completely different from my imagination.

I pushed myself to learn and implement the Boyer Moore from the geeksforgeeks. I got the topic finally, and still
couldn't make the implementation since the code in the website is completely different from what I thought. So I partially
copied the the implementation on the website. 

After I waste so much time learning Boyer Moore and still couldn't make a simple implementation, I couldn't push myself
to make a creative algorithm, so I asked GPT to make a hybrid string matching algorithm. It showed no performance in any
test so I removed it directly. Then I asked GPT again to make a preanalysis algorithm since I couldn't reason on any better preanalysis that you've already implemented.

So, I'm to trying to make a progress in algorithms but I've lost my patience and tolerance, and not sure I'm going to do this engineering job with pleasure in my life.

Everything aside, thank you for your homework and your great efforts to teach us engineering. __I will try to pull myself together and do my very best.__

## References
* **website_url_1:** https://www.geeksforgeeks.org/dsa/boyer-moore-algorithm-for-pattern-searching/
* **website_url_2:** https://chatgpt.com/share/693bd587-5340-8013-a31b-b739d6f3bd22

**Enes ELDEŞ - 22050111017**