# memzocards
memzocards.space
- A flashcard app

# Practical Spring Bean Scope Plan for a Flashcard App
- Layer / Component	Recommended Scope	Notes
- Controllers	singleton	They’re stateless; one per context is fine
- Services	singleton	Core business logic, stateless
- Repositories	singleton	Already thread-safe
- DTO Mappers / Converters	singleton	Stateless, reusable
- Flashcard Study Session	prototype	Optional: if you want per-study-session state
- Request-specific beans	request	Optional: audit logs or temporary request data
- Session-specific beans	session	Rare: mostly for traditional web apps
- Global caches / metrics	application	Optional: store app-wide metrics
- WebSocket user session	websocket	Optional: track live user progress





Controller → Service → Repository

             ↓
             
            DTO
            
             ↕
             
           Entity

🔥 The real flow (what actually happens)

Let’s walk through a request:

📥 Request comes in
POST /flashcards
1. Controller
receives JSON
maps → DTO
public FlashcardDTO create(@RequestBody FlashcardDTO dto)
2. Service
takes DTO
converts → Entity
applies logic
Flashcard entity = mapper.toEntity(dto);
3. Repository
works ONLY with Entity
repository.save(entity);
4. Back up the chain
Entity → DTO
returned to client           
