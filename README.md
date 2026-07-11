# jenniferlakey.github.io
# Jennifer Lakey – Professional Portfolio

This repository contains the source code for my professional portfolio, originally created as my CS‑499 Computer Science Capstone project and later expanded into a full multi‑domain professional site. What began as an academic exercise evolved into a long‑term career asset that showcases my work in technical project management, software engineering, and STEM leadership.

The site is intentionally engineered as a lightweight, maintainable static website using vanilla HTML and CSS, developed locally in Visual Studio Code and deployed through GitHub Pages.

---

## Tech Stack

### Frontend
- Vanilla HTML5 and CSS3  
- Custom reusable components (`.card`, `.artifact-links`, navigation bar)  
- Responsive layout without frameworks for clarity and maintainability  
- Minimal JavaScript (only where needed)

### Development Environment
- Built locally using **Visual Studio Code**  
- Manual file structuring and page organization  
- Version control with **Git**  
- Repository hosting and deployment via **GitHub**

---

## Site Structure

The portfolio is organized into standalone HTML pages, each representing a major professional domain:

- `/` – Home page  
- `/project-management/` – Project management portfolio  
- `/computer-science/` – Computer science portfolio (original capstone)  
- `/skills/` – Skills overview  
- `/resume/` – HTML resume + downloadable PDF  
- `/contact/` – Contact page  

Each portfolio contains multiple subfolders, each with its own `index.html` and supporting files.

---

## Directory Structure

```plaintext
/
├── index.html
├── computer-science/
│   ├── index.html
│   ├── artifact-one/
│   │   ├── index.html
│   │   ├── enhanced/
│   │   ├── original/
│   │   └── images/
│   ├── artifact-two/
│   │   ├── index.html
│   │   ├── enhanced/
│   │   ├── original/
│   │   └── images/
│   └── artifact-three/
│       ├── index.html
│       ├── enhanced/
│       └── original/
├── project-management/
│   ├── index.html
│   ├── governance-organizational/
│   │   ├── index.html
│   │   ├── QSO 440 Module Four Case Study Analysis.docx
│   │   ├── QSO 440 Module Four Case Study Analysis.pdf
│   │   ├── QSO 440 Module Seven Case Study Analysis.docx
│   │   └── QSO 440 Module Seven Case Study Analysis.pdf
│   ├── planning-scope/
│   │   ├── index.html
│   │   ├── QSO 440 Module One Short Paper.docx
│   │   ├── QSO 440 Module One Short Paper.pdf
│   │   ├── QSO 440 Module Two Project Proposal Short Paper.docx
│   │   └── QSO 440 Module Two Project Proposal Short Paper.pdf
│   ├── risk-control-performance/
│   │   ├── index.html
│   │   ├── QSO 440 Discussion 4-1.pdf
│   │   ├── QSO 440 Module Eight Short Paper Scope Creep.docx
│   │   ├── QSO 440 Module Eight Short Paper Scope Creep.pdf
│   │   ├── QSO 440 Module Six Earned Value Analysis.docx
│   │   └── QSO 440 Module Six Earned Value Analysis.pdf
│   ├── stakeholder-communication/
│   │   ├── index.html
│   │   ├── QSO 440 Module Five Stakeholder Analysis.docx
│   │   └── QSO 440 Module Five Stakeholder Analysis.pdf
│   └── wbs-structure/
│       ├── index.html
│       ├── QSO 440 Module Three WBS Short Paper.docx
│       └── QSO 440 Module Three WBS Short Paper.pdf
├── skills/
│   └── index.html
├── resume/
│   ├── index.html
│   └── resume.pdf
└── contact/
    └── index.html
```

This structure reflects the original capstone artifacts and the project management coursework, organized into topic‑based folders with their own HTML entry points.

---

## Development History (Project Evolution)

This portfolio began as my **CS‑499 Capstone Project**, where I built a structured computer science portfolio demonstrating software engineering, algorithms, and technical documentation through three core artifacts.

As I approached graduation, I made a strategic decision to transform the project into a **professional career asset** rather than leaving it as a single academic submission.

The evolution included:

1. **Refining the original Computer Science portfolio**  
   - Kept the artifact structure (`artifact-one`, `artifact-two`, `artifact-three`) with enhanced, original, and image assets  
   - Improved explanations, documentation, and navigation  

2. **Building a Project Management portfolio**  
   - Created topic‑based sections (planning/scope, risk/control/performance, stakeholder communication, governance, WBS structure)  
   - Integrated QSO 440 coursework (case studies, short papers, analyses) into web‑accessible artifacts  

3. **Creating a unified landing page**  
   - Designed a professional home page focused on leadership, strengths, and what I do  
   - Connected both portfolios into a cohesive narrative

4. **Adding career‑critical pages**  
   - `/skills/` – consolidated technical, PM, and cognitive strengths  
   - `/resume/` – HTML resume plus downloadable PDF for recruiters  
   - `/contact/` – simple, professional contact page  

This transformation demonstrates my ability to plan, iterate, and deliver structured technical work aligned with long‑term career goals.

---

## Deployment Pipeline

This site is deployed using **GitHub Pages**, which automatically serves the repository as a static website.

- **Branch:** `main`  
- **Hosting:** GitHub Pages  
- **Build system:** None (vanilla HTML/CSS)  
- **Workflow:**  
  - Developed locally in VS Code  
  - Committed and pushed to GitHub  
  - GitHub Pages automatically rebuilds and deploys on each push  

---

## Resume Integration

The `/resume/` page includes:

- A fully formatted HTML version of my resume  
- A downloadable PDF (`resume.pdf`)  
- A styled download button for easy access  

---

## Purpose

This portfolio is designed to:

- Demonstrate my technical project management capabilities  
- Show my software engineering foundation  
- Provide structured examples of my academic and technical work  
- Present a polished, professional online presence for recruiters and collaborators  
- Serve as a long‑term, maintainable platform I can extend as my career grows  

---

## Design Philosophy

This portfolio is intentionally engineered as a lightweight, modular, and maintainable static website. I chose **vanilla HTML and CSS** instead of frameworks to ensure full control over structure, styling, and performance. This approach keeps the site transparent, easy to extend, and free from unnecessary dependencies.

The layout is built around reusable components such as `.card`, `.artifact-links`, and a consistent navigation bar. These elements create visual cohesion across pages while keeping the codebase simple and predictable. Each domain—project management, computer science, skills, resume, and contact—is isolated into its own folder with a dedicated `index.html`, reinforcing modularity and making updates straightforward.

I developed the site locally in **Visual Studio Code**, using manual file organization to mirror the conceptual structure of the content. This hands-on approach reflects my preference for clarity, intentional architecture, and maintainable design. Git and GitHub Pages provide a clean deployment pipeline that aligns with the simplicity of the site: every commit represents a deliberate change, and every push triggers an automatic deployment.

Overall, the design emphasizes clarity, structure, and long-term maintainability—values that mirror my approach to technical project management and software engineering.

---

## Future Improvements & Roadmap

This portfolio is designed to evolve alongside my career. Planned enhancements include:

### **Structural & Content Enhancements**
- Expand the Project Management and Computer Science portfolios with additional artifacts and case studies.
- Add new sections for future professional work, certifications, and continuing education.
- Introduce a “Featured Projects” area on the home page to highlight major accomplishments.

### **Technical Improvements**
- Add lightweight JavaScript enhancements for improved navigation and interactivity.
- Implement accessibility improvements (ARIA labels, semantic refinements, contrast checks).
- Add optional dark mode using CSS variables for improved user experience.

### **Documentation & Process**
- Add contribution guidelines and a changelog to reflect ongoing updates.
- Introduce versioning for major site revisions.
- Expand the README with architectural diagrams or workflow illustrations.

### **Deployment & Automation**
- Add a GitHub Actions workflow for automated HTML validation and link checking.
- Explore optional static site generation (e.g., Jekyll) if future complexity increases.

These improvements reflect my commitment to iterative development, structured planning, and long-term maintainability, core principles in both project management and engineering.

---

## Contact

You can reach me through the contact page on the site or via email at:  
**jenlakey1986@gmail.com**


---