## Journey (10%) – MANDATORY

This section describes my learning journey during this homework process.

Since I did not previously know the **Boyer–Moore string matching algorithm**, I first tried to understand the homework requirements by sharing the GitHub repository with Gemini and asking for a detailed explanation of the code and structure.

- GitHub explanation via Gemini:  
  https://gemini.google.com/share/b0a040472ea6

After realizing that I lacked the theoretical background of the Boyer–Moore algorithm, I watched several YouTube videos to understand the core idea, including bad character and good suffix heuristics.

- YouTube resources:
    - https://www.youtube.com/watch?v=4Xyhb72LCX4
    - https://www.youtube.com/shorts/IyxdywkNdQk
    - https://youtu.be/O4RXMKHXTFE?si=8i9tWMBJn12DqpUw

These videos helped me grasp the general intuition of the algorithm, but I still had difficulty understanding how the algorithm progresses step by step during execution.

To overcome this, I used **ChatGPT** to deepen my understanding of the Boyer–Moore algorithm. I specifically asked for multiple examples and step-by-step simulations so that I could clearly see how indices move, how shifts are calculated, and how the algorithm behaves in different scenarios.

- ChatGPT discussion and examples:  
  https://chatgpt.com/share/693bc484-eb78-8011-b86d-382575219e23

Additionally, I focused on understanding the **pre-processing (Pre-Analysis)** phase of the algorithm, since it is critical for performance and was initially confusing for me. I analyzed the pre-processing tables in detail using Gemini to better understand how they are constructed and used during matching.

- Pre-analysis exploration via Gemini:  
  https://gemini.google.com/app/afe0d25eb5f90787

### What I Learned
- The fundamental idea behind the Boyer–Moore algorithm and why it is efficient.
- How bad character and good suffix heuristics work.
- The importance of the pre-processing phase and how it affects runtime.
- How to trace an algorithm step by step instead of only understanding it conceptually.

### Challenges
- Understanding the pre-processing tables was the most challenging part.
- Visualizing how indices shift during mismatches required multiple examples.
- Translating theoretical knowledge into actual code comprehension took time.

### Overall Opinion
This homework was challenging but very educational. It forced me to combine multiple learning resources (videos, AI tools, and code analysis) and helped me develop a deeper algorithmic thinking process rather than just writing code.

### Feedback
Providing a small step-by-step example in the homework description could make the assignment more beginner-friendly, especially for students encountering Boyer–Moore for the first time.
