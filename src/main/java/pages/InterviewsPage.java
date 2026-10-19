package pages;

import components.RoundedLineBorder;
import database.Database;
import models.InterviewSchedule;
import users.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterviewsPage extends Page {

    private final List<InterviewScheduleEntry> schedules;
    private final Map<LocalDate, List<InterviewScheduleEntry>> schedulesByDate = new HashMap<>();
    private YearMonth currentMonth = YearMonth.now();

    private JLabel miniMonthLabel;
    private JLabel mainMonthLabel;
    private JPanel miniGrid;
    private JPanel mainGrid;

    private static final DateTimeFormatter DB_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter MONTH_LABEL = DateTimeFormatter.ofPattern("MMMM yyyy");
    private static final DateTimeFormatter DAY_LABEL = DateTimeFormatter.ofPattern("d");
    private static final DateTimeFormatter POPUP_DATE = DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy");
    private static final DateTimeFormatter POPUP_TIME = DateTimeFormatter.ofPattern("h:mm a");
    private static final DateTimeFormatter SLOT_TIME = DateTimeFormatter.ofPattern("h:mm a");

    public InterviewsPage(User user) {
        super(user);
        schedules = loadInterviewSchedules();
        indexSchedulesByDate();

        addBlock(makeHeading("Interviews",
                "View your interview schedules in a calendar layout.", null), 15);

        JPanel card = new JPanel(new BorderLayout(14, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(Color.decode("#d7dce3"), 14, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        int fullHeight = getCalendarCardHeight();
        card.setPreferredSize(new Dimension(0, fullHeight));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, fullHeight));
        card.setMinimumSize(new Dimension(0, fullHeight));

        JPanel miniWrap = new JPanel(new BorderLayout());
        miniWrap.setOpaque(false);
        miniWrap.add(makeMiniCalendarPanel(), BorderLayout.NORTH);
        card.add(miniWrap, BorderLayout.WEST);
        card.add(makeMainCalendarPanel(), BorderLayout.CENTER);

        addBlock(card, 0);
        refreshCalendars();
    }

    private int getCalendarCardHeight() {
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        return Math.max(640, screenHeight - 230);
    }

    private List<InterviewScheduleEntry> loadInterviewSchedules() {
        List<InterviewScheduleEntry> items = new ArrayList<>();
        List<InterviewSchedule> dbSchedules;
        if ("Employer".equalsIgnoreCase(getUser().getRole())) {
            dbSchedules = Database.getInterviewSchedulesForEmployer(getUser().getEmail());
        } else {
            dbSchedules = Database.getInterviewSchedulesForSeeker(getUser().getEmail());
        }
        for (InterviewSchedule schedule : dbSchedules) {
            LocalDateTime dateTime = parseScheduleDateTime(schedule.getScheduledAt());
            if (dateTime == null) continue;
            items.add(new InterviewScheduleEntry(schedule, dateTime));
        }
        items.sort(Comparator.comparing(s -> s.dateTime));
        return items;
    }

    private void indexSchedulesByDate() {
        schedulesByDate.clear();
        for (InterviewScheduleEntry schedule : schedules) {
            schedulesByDate.computeIfAbsent(schedule.dateTime.toLocalDate(), k -> new ArrayList<>()).add(schedule);
        }
    }

    private LocalDateTime parseScheduleDateTime(String dateTimeText) {
        if (dateTimeText == null) return null;
        String cleaned = dateTimeText.trim();
        try {
            return LocalDateTime.parse(cleaned.substring(0, 19), DB_DATE_TIME);
        } catch (Exception ignored) {
            return null;
        }
    }

    private JPanel makeMiniCalendarPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        Dimension miniSize = new Dimension(180, 210);
        panel.setPreferredSize(miniSize);
        panel.setMinimumSize(miniSize);
        panel.setMaximumSize(miniSize);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(Color.decode("#e5e9f0"), 10, 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        JPanel top = new JPanel(new BorderLayout(6, 0));
        top.setOpaque(false);
        JButton prev = navButton("‹");
        JButton next = navButton("›");
        miniMonthLabel = new JLabel("", SwingConstants.CENTER);
        miniMonthLabel.setFont(new Font("Inter", Font.BOLD, 14));
        top.add(prev, BorderLayout.WEST);
        top.add(miniMonthLabel, BorderLayout.CENTER);
        top.add(next, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        miniGrid = new JPanel(new GridLayout(7, 7, 0, 0));
        miniGrid.setOpaque(false);
        panel.add(miniGrid, BorderLayout.CENTER);

        prev.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            refreshCalendars();
        });
        next.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            refreshCalendars();
        });

        return panel;
    }

    private JPanel makeMainCalendarPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        nav.setOpaque(false);
        JButton prev = navButton("‹");
        JButton next = navButton("›");
        mainMonthLabel = new JLabel();
        mainMonthLabel.setFont(new Font("Inter", Font.BOLD, 24));
        mainMonthLabel.setForeground(Color.decode("#1d4ed8"));
        nav.add(prev);
        nav.add(mainMonthLabel);
        nav.add(next);
        header.add(nav, BorderLayout.WEST);
        panel.add(header, BorderLayout.NORTH);

        JPanel week = new JPanel(new GridLayout(1, 7));
        week.setOpaque(false);
        String[] dayNames = {"MON", "TUE", "WED", "THUR", "FRI", "SAT", "SUN"};
        for (String day : dayNames) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            dayLabel.setFont(new Font("Inter", Font.PLAIN, 12));
            dayLabel.setForeground(Color.decode("#9ca3af"));
            dayLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.decode("#e5e7eb")));
            week.add(dayLabel);
        }

        mainGrid = new JPanel(new GridLayout(6, 7, 0, 0));
        mainGrid.setOpaque(false);

        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setOpaque(false);
        centerWrap.add(week, BorderLayout.NORTH);
        centerWrap.add(mainGrid, BorderLayout.CENTER);
        panel.add(centerWrap, BorderLayout.CENTER);

        prev.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            refreshCalendars();
        });
        next.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            refreshCalendars();
        });

        return panel;
    }

    private JButton navButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Inter", Font.BOLD, 16));
        button.setFocusable(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBackground(Color.WHITE);
        button.setBorder(new RoundedLineBorder(Color.decode("#e5e9f0"), 8, 1));
        return button;
    }

    private void refreshCalendars() {
        miniMonthLabel.setText(currentMonth.format(MONTH_LABEL));
        mainMonthLabel.setText(currentMonth.format(MONTH_LABEL));
        renderMiniGrid();
        renderMainGrid();
    }

    private void renderMiniGrid() {
        miniGrid.removeAll();
        String[] shortDays = {"M", "T", "W", "T", "F", "S", "S"};
        for (String d : shortDays) {
            JLabel label = new JLabel(d, SwingConstants.CENTER);
            label.setFont(new Font("Inter", Font.PLAIN, 10));
            label.setForeground(Color.decode("#9ca3af"));
            miniGrid.add(label);
        }

        LocalDate start = calendarStart(currentMonth);
        for (int i = 0; i < 42; i++) {
            LocalDate date = start.plusDays(i);
            JLabel day = new JLabel(String.valueOf(date.getDayOfMonth()), SwingConstants.CENTER);
            day.setFont(new Font("Inter", Font.PLAIN, 10));
            day.setOpaque(true);
            boolean inMonth = date.getMonth() == currentMonth.getMonth();
            day.setForeground(inMonth ? Color.decode("#111827") : Color.decode("#9ca3af"));
            day.setBackground(date.equals(LocalDate.now()) ? Color.decode("#e1efff") : Color.WHITE);
            day.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.decode("#edf2f7")));
            miniGrid.add(day);
        }
        miniGrid.revalidate();
        miniGrid.repaint();
    }

    private void renderMainGrid() {
        mainGrid.removeAll();
        LocalDate start = calendarStart(currentMonth);
        for (int i = 0; i < 42; i++) {
            LocalDate date = start.plusDays(i);
            mainGrid.add(makeDayCell(date));
        }
        mainGrid.revalidate();
        mainGrid.repaint();
    }

    private LocalDate calendarStart(YearMonth month) {
        LocalDate first = month.atDay(1);
        int dayOffset = first.getDayOfWeek().getValue() - 1; // MON = 1
        return first.minusDays(dayOffset);
    }

    private JPanel makeDayCell(LocalDate date) {
        JPanel cell = new JPanel(new BorderLayout());
        cell.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.decode("#e5e7eb")));
        cell.setBackground(date.getMonth() == currentMonth.getMonth() ? Color.WHITE : Color.decode("#f9fafb"));

        JLabel dayNumber = new JLabel(date.format(DAY_LABEL));
        dayNumber.setFont(new Font("Inter", Font.PLAIN, 13));
        dayNumber.setForeground(date.getMonth() == currentMonth.getMonth()
                ? Color.decode("#111827")
                : Color.decode("#9ca3af"));
        dayNumber.setBorder(BorderFactory.createEmptyBorder(6, 8, 0, 0));
        cell.add(dayNumber, BorderLayout.NORTH);

        JPanel slots = new JPanel();
        slots.setOpaque(false);
        slots.setLayout(new BoxLayout(slots, BoxLayout.Y_AXIS));
        slots.setBorder(BorderFactory.createEmptyBorder(2, 6, 4, 6));

        List<InterviewScheduleEntry> daySchedules = schedulesByDate.getOrDefault(date, new ArrayList<>());
        int shown = 0;
        for (InterviewScheduleEntry schedule : daySchedules) {
            if (shown == 2) break;
            JButton slotBtn = new JButton(SLOT_TIME.format(schedule.dateTime) + "  " + compact(schedule.schedule.getJobTitle(), 16));
            slotBtn.setFont(new Font("Inter", Font.PLAIN, 9));
            slotBtn.setHorizontalAlignment(SwingConstants.LEFT);
            slotBtn.setFocusable(false);
            slotBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            slotBtn.setForeground(Color.decode("#ea580c"));
            slotBtn.setBackground(Color.decode("#fff7ed"));
            slotBtn.setBorder(new RoundedLineBorder(Color.decode("#fed7aa"), 6, 1));
            slotBtn.setMargin(new Insets(2, 4, 2, 4));
            slotBtn.addActionListener(e -> showInterviewDetails(schedule));
            slots.add(slotBtn);
            slots.add(Box.createVerticalStrut(3));
            shown++;
        }
        if (daySchedules.size() > shown) {
            JLabel more = new JLabel("+" + (daySchedules.size() - shown) + " more");
            more.setFont(new Font("Inter", Font.PLAIN, 9));
            more.setForeground(Color.decode("#6b7280"));
            slots.add(more);
        }

        cell.add(slots, BorderLayout.CENTER);
        return cell;
    }

    private void showInterviewDetails(InterviewScheduleEntry schedule) {
        String counterpartLabel = "Employer".equalsIgnoreCase(getUser().getRole()) ? "Candidate" : "Company";
        String message = "Job Title: " + schedule.schedule.getJobTitle()
                + "\n" + counterpartLabel + ": " + schedule.schedule.getCompany()
                + "\nJob Location: " + schedule.schedule.getJobLocation()
                + "\nDate: " + POPUP_DATE.format(schedule.dateTime)
                + "\nTime: " + POPUP_TIME.format(schedule.dateTime)
                + "\nInterview Mode: " + fallback(schedule.schedule.getInterviewMode())
                + "\nInterview Location: " + fallback(schedule.schedule.getInterviewLocation())
                + "\nNotes: " + fallback(schedule.schedule.getNotes());
        JOptionPane.showMessageDialog(this, message, "Interview Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private String fallback(String text) {
        return (text == null || text.isBlank()) ? "Not specified" : text;
    }

    private String compact(String text, int max) {
        if (text == null) return "";
        if (text.length() <= max) return text;
        return text.substring(0, Math.max(0, max - 1)) + "…";
    }

    private static class InterviewScheduleEntry {
        private final InterviewSchedule schedule;
        private final LocalDateTime dateTime;

        private InterviewScheduleEntry(InterviewSchedule schedule, LocalDateTime dateTime) {
            this.schedule = schedule;
            this.dateTime = dateTime;
        }
    }
}
