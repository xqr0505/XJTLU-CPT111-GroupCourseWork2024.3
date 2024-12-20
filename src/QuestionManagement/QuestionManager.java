package QuestionManagement;

import xjtlu.cpt111.assignment.quiz.model.Difficulty;
import xjtlu.cpt111.assignment.quiz.model.Option;
import xjtlu.cpt111.assignment.quiz.model.Question;
import xjtlu.cpt111.assignment.quiz.util.IOUtilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class QuestionManager {

private final Map<String, Map<Difficulty, List<Question>>> m_question_ = new HashMap<>();

/**
 * Get all questions.
 *
 * @return a map of all questions grouped by topic and difficulty.
 */
public Map<String, Map<Difficulty, List<Question>>> GetQuestions() {
  return m_question_;
}

public String[] GetTopics() {
  return m_question_.keySet()
                    .toArray(new String[0]);
}

/**
 * Get questions for a specific topic.
 *
 * @param topic the topic to retrieve questions for.
 * @return a map of questions grouped by difficulty for the specified topic.
 */
public Map<Difficulty, List<Question>> GetQuestions(String topic) {
  return m_question_.get(topic);
}

public static class QuestionLoader {
  private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
    return t -> Boolean.TRUE.equals(seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE));
  }

  /**
   * Loads questions from a file.
   *
   * @param fp the file path.
   * @return ‘QuestionManager’ with loaded questions.
   */
  public static QuestionManager LoadQuestion(String fp) {
    var questionManagement = new QuestionManager();
    Arrays.stream(IOUtilities.readQuestions(fp))
          .collect(Collectors.groupingBy(Question::getTopic))
          .forEach((x, y) -> questionManagement.m_question_.put(x, y.stream()
                                                                    .filter(q -> q.getOptions().length > 1 &&
                                                                                 ! q.getQuestionStatement()
                                                                                    .isEmpty() &&
                                                                                 Arrays.stream(q.getOptions())
                                                                                       .filter(Option::isCorrectAnswer)
                                                                                       .count() == 1)
                                                                    .toList()
                                                                    .stream()
                                                                    .collect(Collectors.groupingBy(
                                                                        Question::getDifficulty))));
    return questionManagement;
  }

}


}
