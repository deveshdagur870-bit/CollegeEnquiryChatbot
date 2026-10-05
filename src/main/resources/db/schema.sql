CREATE TABLE IF NOT EXISTS intents (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    keywords TEXT NOT NULL,
    question TEXT,
    answer TEXT NOT NULL,
    category TEXT
);

CREATE TABLE IF NOT EXISTS students (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    roll_no TEXT UNIQUE NOT NULL,
    name TEXT NOT NULL,
    course TEXT,
    year INTEGER,
    fee_status TEXT
);

CREATE TABLE IF NOT EXISTS chat_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_message TEXT,
    bot_response TEXT,
    token_estimate INTEGER,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO intents (keywords, question, answer, category) VALUES
('admission,admissions,eligibility,apply', 'How to apply for admission?', 'Admission ke liye college ki official website par jaake online form fill karna hoga. Eligibility: 10+2 with 50% marks. Last date usually June-July mein hoti hai.', 'admission'),
('fee,fees,structure,payment', 'What is the fee structure?', 'Fee structure course ke hisaab se alag hai. B.Tech ke liye average annual fee ₹80,000-1,20,000 hai. Exact details Accounts Office se confirm karein.', 'fees'),
('course,courses,branch,branches', 'What courses are offered?', 'College mein ye courses available hain: B.Tech (CSE, ECE, ME, CE), BCA, MCA, MBA. Detailed syllabus website par milega.', 'academics'),
('hostel,accommodation,room', 'Is hostel available?', 'Haan, boys aur girls dono ke liye separate hostel available hai with mess facility. Hostel fee alag se lagti hai.', 'facilities'),
('library,timing,timings,books,issue', 'What are library timings?', 'Library subah 8 AM se raat 8 PM tak khuli rehti hai, Monday to Saturday. Students ek time mein 3 books, 15 days ke liye issue kar sakte hain.', 'facilities'),
('placement,placements,job,jobs,package', 'What about placements?', 'College ka placement cell top companies ke saath tie-up rakhta hai. Average package ₹4-6 LPA aur highest package ₹15+ LPA raha hai recent years mein.', 'placements'),
('exam,exams,schedule,datesheet', 'When are exams held?', 'Semester exams generally December aur May mein hote hain. Exact datesheet exam cell ki notice board / website par publish hoti hai.', 'academics'),
('contact,phone,email,address', 'How to contact the college?', 'Aap college office ko is number par contact kar sakte hain: 0123-4567890, ya email karein info@college.edu.', 'general'),
('scholarship,scholarships', 'Are scholarships available?', 'Haan, merit-based aur government scholarships (jaise post-matric) dono available hain. Scholarship cell se detail lein.', 'fees'),
('hi,hello,hey,namaste', 'greeting', 'Namaste! Bataiye, aapko admission, fees, courses, hostel, placement ya kisi aur cheez ke baare mein jaankari chahiye?', 'general');

INSERT INTO students (roll_no, name, course, year, fee_status) VALUES
('CS101', 'Rahul Sharma', 'B.Tech CSE', 3, 'Paid'),
('CS102', 'Priya Verma', 'B.Tech CSE', 2, 'Pending'),
('ME201', 'Aman Singh', 'B.Tech ME', 1, 'Paid'),
('MBA301', 'Neha Gupta', 'MBA', 2, 'Paid');
