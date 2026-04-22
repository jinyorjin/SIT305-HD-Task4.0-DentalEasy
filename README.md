<img width="300" height="500" alt="image" src="https://github.com/user-attachments/assets/a8848293-3c11-413b-824a-de236ee842f5" />


DentalEasy: Project Objectives and Technical Approach
Primary Purpose
The main goal of DentalEasy is to simplify dental communication. Clinical terms and post-operative instructions are often too complex for patients to follow. I developed this app to act as a bridge, translating professional jargon into layman's terms to improve patient health literacy and reduce anxiety.

Technical Scope with Java
I chose Java as the core language for this project to ensure a stable and reliable development environment. By leveraging Java’s object-oriented structure, I focused on building a clean codebase that can handle clinical data safely. This choice provides the necessary foundation for integrating complex features in the future.

Architectural Goal: MVVM Implementation
One of the key technical objectives was to implement a professional MVVM (Model-View-ViewModel) architecture.

Separation of Logic: I separated the UI components (Activity) from the business logic (ViewModel). This prevents the code from becoming "spaghetti" and makes it much easier to maintain or update specific features.

State Management: Using ViewModels ensures that data remains consistent even during configuration changes, such as screen rotations, providing a smoother user experience.

Infrastructure for AI Integration
A major objective was to build a "ready-to-scale" infrastructure. While the current version uses logic-based responses, the backend structure is designed to be fully compatible with Large Language Models (LLMs) like Llama 3.2. The goal was to create a modular system where AI APIs can be plugged in without rewriting the core UI.

UX/UI Strategy
I used XML and Material Design components, specifically CardView, to create a professional yet approachable look. The objective was to design an interface that feels like a trustworthy medical tool, organizing information into clear categories to minimize cognitive load for the patient.

Development Environment
The project was built using the Android SDK with JDK 17 (jbr-17) and managed through Gradle. Standardizing the build environment and maintaining version control via Git were essential parts of the development process to ensure professional-grade software delivery.
