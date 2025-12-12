🚀 Yolculuğum

Bu ödev sürecinde farklı string matching algoritmalarını hem teorik hem pratik olarak inceledim. Öncelikle Boyer-Moore algoritmasını bad character rule ile uyguladım. Burada karakterlerin son görüldüğü indeksleri tutan bir tablo oluşturdum ve arama sırasında sağdan sola karşılaştırma yaptım. Mismatch durumunda tabloyu kullanarak kaydırma yaptım. Bu yaklaşım gereksiz karşılaştırmaları azalttı. Edge case’ler (örneğin boş pattern veya pattern’in text’ten uzun olması) için özel kontroller ekledim.

Kendi tasarladığım GoCrazy algoritması ise hibrit bir yaklaşım oldu. Küçük pattern’lerde Naive algoritmayı kullandım çünkü overhead düşük. Uzun pattern’lerde ise rolling hash kullandım. Hash eşleştiğinde karakterleri doğrudan kontrol ettim. Bu tasarım basitlik ve hız arasında bir denge kurmayı hedefledi.
🔎 Ön Analiz Stratejisi

Bir StudentPreAnalysis modülü tasarladım. Burada pattern uzunluğu, text uzunluğu, alfabe çeşitliliği ve tekrar oranı gibi kriterlere göre hangi algoritmanın seçileceğine karar verdim. Örneğin:

    Pattern ≤ 3 → Naive

    Text > 800 → GoCrazy

    Pattern > 15 ve alfabe > 6 → Boyer-Moore

    Tekrar oranı > 0.15 → KMP

    Orta uzunlukta pattern (5–12) ve text > 300 → Rabin-Karp

    Diğer durumlar → Naive

Bu kurallar sayesinde farklı senaryolarda doğru algoritmayı seçmeye çalıştım.

📊 Sonuçların Analizi

Testlerde gördüm ki:

    Naive birçok durumda en hızlıydı.

    KMP tekrar eden pattern’lerde başarılı oldu.

    GoCrazy uzun textlerde fayda sağladı.

    Boyer-Moore uzun ve çeşitli alfabelerde iyi performans verdi ama setup maliyeti vardı.

    Rabin-Karp orta uzunlukta patternlerde işe yaradı.

Doğru seçim oranım yaklaşık %60 civarında oldu.

📚 Araştırmalarım

Bu süreçte bazı AI modellerinden destek aldım. Özellikle GoCrazy algoritması için hibrit yaklaşım ve rolling hash tasarımı konusunda fikir aldım. Ayrıca StudentPreAnalysis stratejisinde eşik değerleri belirlerken AI modellerinden öneriler aldım. 

✍️ Kendi Yolculuğum

Bu ödev bana algoritmaların pratikte nasıl davrandığını gösterdi. Özellikle threshold belirlemek zordu; bazen kurallarım fazla agresif oldu ve doğruluk düştü. Naive algoritmanın beklenenden daha güçlü olduğunu görmek ilginçti. Genel olarak hem kodlama hem de analiz becerilerimi geliştiren faydalı bir deneyim oldu.

Zühtü Eren İncekara – 22050111023 Zekeriya Damcı – 22050111074
