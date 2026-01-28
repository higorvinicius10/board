import java.util.*;

// ==========================================
// 1. MODELO (ENTIDADE)
// ==========================================
enum TaskStatus {
    TODO("A Fazer"), DOING("Em Progresso"), DONE("Concluído");
    private final String label;
    TaskStatus(String label) { this.label = label; }
    public String getLabel() { return label; }
}

class Task {
    private Long id;
    private String title;
    private TaskStatus status;

    public Task(Long id, String title, TaskStatus status) {
        this.id = id;
        this.title = title;
        this.status = status;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("[%s] ID: %d | %s", status, id, title);
    }
}

// ==========================================
// 2. REPOSITÓRIO (DADOS)
// ==========================================
class TaskRepository {
    private final Map<Long, Task> database = new HashMap<>();
    private long idSequence = 1;

    public Task save(Task task) {
        if (task.getId() == null) {
            task = new Task(idSequence++, task.getTitle(), task.getStatus());
        }
        database.put(task.getId(), task);
        return task;
    }

    public List<Task> findAll() {
        return new ArrayList<>(database.values());
    }

    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(database.get(id));
    }
}

// ==========================================
// 3. SERVIÇO (REGRAS DE NEGÓCIO)
// ==========================================
class TaskService {
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public void addTask(String title) {
        if (title == null || title.trim().isEmpty()) {
            System.err.println("Erro: Título inválido.");
            return;
        }
        repository.save(new Task(null, title, TaskStatus.TODO));
    }

    public void moveTask(Long id) {
        repository.findById(id).ifPresentOrElse(task -> {
            if (task.getStatus() == TaskStatus.TODO) task.setStatus(TaskStatus.DOING);
            else if (task.getStatus() == TaskStatus.DOING) task.setStatus(TaskStatus.DONE);
            System.out.println("Tarefa atualizada com sucesso!");
        }, () -> System.err.println("Erro: Tarefa não encontrada."));
    }

    public void showBoard() {
        List<Task> all = repository.findAll();
        System.out.println("\n===== KANBAN BOARD =====");
        for (TaskStatus s : TaskStatus.values()) {
            System.out.println("\n--- " + s.getLabel() + " ---");
            all.stream()
                    .filter(t -> t.getStatus() == s)
                    .forEach(System.out::println);
        }
        System.out.println("========================\n");
    }
}

// ==========================================
// 4. APLICAÇÃO (INTERFACE)
// ==========================================
public class Main {
    public static void main(String[] args) {
        TaskRepository repo = new TaskRepository();
        TaskService service = new TaskService(repo);
        Scanner scanner = new Scanner(System.in);
        int option = -1;

        System.out.println("Bem-vindo ao Java Task Board!");

        while (option != 0) {
            System.out.println("1. Adicionar Tarefa | 2. Mover Tarefa | 3. Ver Board | 0. Sair");
            System.out.print("Escolha: ");
            try {
                option = Integer.parseInt(scanner.nextLine());

                switch (option) {
                    case 1 -> {
                        System.out.print("Título da tarefa: ");
                        service.addTask(scanner.nextLine());
                    }
                    case 2 -> {
                        System.out.print("ID da tarefa para avançar: ");
                        service.moveTask(Long.parseLong(scanner.nextLine()));
                    }
                    case 3 -> service.showBoard();
                    case 0 -> System.out.println("Saindo...");
                    default -> System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.err.println("Entrada inválida. Tente novamente.");
            }
        }
        scanner.close();
    }
}
