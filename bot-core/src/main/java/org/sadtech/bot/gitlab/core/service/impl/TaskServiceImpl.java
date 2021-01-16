package org.sadtech.bot.gitlab.core.service.impl;

//@Service
//public class TaskServiceImpl extends AbstractSimpleManagerService<Task, Long> implements TaskService {
//
//    private static final Pattern PATTERN = Pattern.compile("@[\\w]+");
//
//    private final TaskRepository taskRepository;
//
//    private final MergeRequestsService mergeRequestsService;
//    private final NotifyService notifyService;
//    private final NoteService noteService;
//
//    private final ConversionService conversionService;
//
//    public TaskServiceImpl(
//            TaskRepository taskRepository,
//            MergeRequestsService mergeRequestsService,
//            NotifyService notifyService,
//            NoteService noteService,
//            ConversionService conversionService
//    ) {
//        super(taskRepository);
//        this.taskRepository = taskRepository;
//        this.mergeRequestsService = mergeRequestsService;
//        this.notifyService = notifyService;
//        this.noteService = noteService;
//        this.conversionService = conversionService;
//    }
//
//    @Override
//    public Task create(@NonNull Task task) {
//        Assert.isNotNull(task.getId(), "При создании объекта должен быть установлен идентификатор");
//        task.getAnswers().clear();
//        final Task newTask = taskRepository.save(task);
//        notifyNewTask(task);
//        notificationPersonal(task);
//        return newTask;
//    }
//
//    @Override
//    public Task update(@NonNull Task task) {
//        final Task oldTask = taskRepository.findById(task.getId())
//                .orElseThrow(() -> new NotFoundException("Задача не найдена"));
//
//        if (!task.getBitbucketVersion().equals(oldTask.getBitbucketVersion())) {
//            oldTask.setDescription(task.getDescription());
//            oldTask.setBitbucketVersion(task.getBitbucketVersion());
//        }
//        updateAnswer(oldTask, task);
//        updateStatus(oldTask, task);
//        oldTask.setStatus(task.getStatus());
//        return taskRepository.save(oldTask);
//    }
//
//    private void updateStatus(Task oldTask, Task task) {
//        final TaskStatus oldStatus = oldTask.getStatus();
//        final TaskStatus newStatus = task.getStatus();
//        if (!oldStatus.equals(newStatus)) {
//            switch (newStatus) {
//                case OPEN:
//                    notifyService.send(
//                            TaskNewNotify.builder()
//                                    .messageTask(task.getDescription())
//                                    .authorName(oldTask.getAuthor())
//                                    .url(oldTask.getUrl())
//                                    .build()
//                    );
//                    break;
//                case RESOLVED:
//                    notifyService.send(
//                            TaskCloseNotify.builder()
//                                    .messageTask(oldTask.getDescription())
//                                    .authorName(oldTask.getAuthor())
//                                    .url(oldTask.getUrl())
//                                    .build()
//                    );
//                    break;
//                default:
//                    throw new NotFoundException("Обработчика типа не существует");
//            }
//            oldTask.setStatus(newStatus);
//        }
//    }
//
//    private void updateAnswer(Task oldTask, Task task) {
////        final Set<Long> oldAnswerIds = oldTask.getAnswers();
////        final Set<Long> newAnswerIds = task.getAnswers();
////        if (!oldAnswerIds.equals(newAnswerIds)) {
////            final Set<Long> existsNewAnswersIds = noteService.existsById(newAnswerIds);
////            final List<Note> newAnswers = noteService.getAllById(existsNewAnswersIds).stream()
////                    .filter(comment -> !oldAnswerIds.contains(comment.getId()))
////                    .collect(Collectors.toList());
////            oldTask.getAnswers().clear();
////            oldTask.setAnswers(existsNewAnswersIds);
////            if (!newAnswers.isEmpty()) {
////                notifyService.send(
////                        AnswerCommentNotify.builder()
////                                .url(oldTask.getUrl())
////                                .youMessage(oldTask.getDescription())
////                                .answers(
////                                        newAnswers.stream()
////                                                .map(answerComment -> Answer.of(answerComment.getAuthor(), answerComment.getMessage()))
////                                                .collect(Collectors.toList())
////                                )
////                                .build()
////                );
////            }
////        }
//    }
//
//    @Override
//    public Long getLastTaskId() {
//        return taskRepository.findFirstByOrderByIdDesc().map(Task::getId).orElse(0L);
//    }
//
//    @Override
//    public Task convert(@NonNull Note note) {
//        noteService.deleteById(note.getId());
//        final Task task = conversionService.convert(note, Task.class);
//        final Task newTask = taskRepository.save(task);
//        notifyNewTask(newTask);
//        return newTask;
//    }
//
//    @Override
//    public List<Task> getAllBetweenDate(@NonNull LocalDateTime dateFrom, @NonNull LocalDateTime dateTo) {
//        return taskRepository.findByCreateDateBetween(dateFrom, dateTo);
//    }
//
//    @Override
//    public List<Task> getAllByResponsibleAndStatus(@NonNull String login, @NonNull TaskStatus status) {
//        return taskRepository.findAllByResponsibleAndStatus(login, status);
//    }
//
//    private void notifyNewTask(Task task) {
//        final MergeRequest mergeRequest = mergeRequestsService.getById(task.getPullRequestId())
//                .orElseThrow(() -> new NotFoundException("ПР не найден"));
//
//        notifyService.send(
//                TaskNewNotify.builder()
//                        .authorName(task.getAuthor())
//                        .messageTask(task.getDescription())
//                        .url(task.getUrl())
////                        .recipients(Collections.singleton(mergeRequest.getAuthor().getId()))
//                        .build()
//        );
//    }
//
//    private void notificationPersonal(@NonNull Task task) {
//        Matcher matcher = PATTERN.matcher(task.getDescription());
//        Set<String> recipientsLogins = new HashSet<>();
//        while (matcher.find()) {
//            final String login = matcher.group(0).replace("@", "");
//            recipientsLogins.add(login);
//        }
//        notifyService.send(
//                CommentNotify.builder()
//                        .authorName(task.getAuthor())
//                        .url(task.getUrl())
//                        .message(task.getDescription())
//                        .build()
//        );
//    }
//
//    @Override
//    public void deleteById(@NonNull Long id) {
//        super.deleteById(id);
//    }
//
//    @Override
//    public ExistsContainer<Task, Long> existsById(@NonNull Collection<Long> collection) {
//        return null;
//    }
//
//}
