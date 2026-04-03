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



RestController → Service → DTO → Entity → Repository
