package com.project.sioscms.apps.discord.service;

import com.project.sioscms.SioscmsApplication;
import com.project.sioscms.apps.attach.domain.entity.AttachFile;
import com.project.sioscms.apps.discord.domain.dto.DiscordMemberDto;
import com.project.sioscms.apps.discord.domain.dto.DiscordMentionDto;
import com.project.sioscms.apps.discord.domain.entity.*;
import com.project.sioscms.apps.discord.domain.repository.*;
import com.project.sioscms.common.ApplicationContextProvider;
import com.project.sioscms.common.utils.jpa.restriction.ChangSolJpaRestriction;
import com.project.sioscms.discord.DiscordBotToken;
import com.project.sioscms.discord.DiscordEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiscordBotApiService {
    @Value("${discord.guild-key}")
    private String GUILD_KEY;

    @Value("${cms.site.uri}")
    private String SITE_URI;

    @Value("${attach.resource.path}")
    private String RESOURCE_PATH;

    private final DiscordMemberRepository discordMemberRepository;
    private final DiscordMentionRepository discordMentionRepository;
    private final DiscordUserMensionRepository discordUserMensionRepository;
    private final LeagueRepository leagueRepository;
    private final LeagueTrackRepository leagueTrackRepository;
    private final LeagueTrackMemberRepository leagueTrackMemberRepository;
    private final LeagueTrackWaitRepository leagueTrackWaitRepository;

    /**
     * JDA 생성 안됐을 경우 재생성 로직(서버에서만 문제생김)
     * @return
     */
    private JDA getJDA() {
        if (SioscmsApplication.getJda() == null) {
            ApplicationContext context = ApplicationContextProvider.getApplicationContext();
            DiscordBotToken token = context.getBean(DiscordBotToken.class);
            JDA jda = JDABuilder.createDefault(token.getToken())
                    .setActivity(Activity.playing("ESK 리그 대기"))
                    .setAutoReconnect(true)
                    .setLargeThreshold(250)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                    .addEventListeners(new DiscordEventListener(context))
                    .build();

            SioscmsApplication.setJda(jda);

            return jda;
        } else {
            return SioscmsApplication.getJda();
        }
//        return null;
    }

    /**
     * 디스코드 길드 가입자 목록 조회
     *
     * @return
     */
    public List<DiscordMemberDto.Response> getDiscordMembers(DiscordMemberDto.Request requestDto) {
        ChangSolJpaRestriction restriction = new ChangSolJpaRestriction();
        restriction.equals("isDeleted", false);
        return discordMemberRepository.findAll(restriction.toSpecification(), Sort.by(Sort.Direction.DESC, "username"))
                .stream().map(DiscordMember::toResponse).collect(Collectors.toList());
    }

    /**
     * 디스코드 공지 채널 목록을 얻어온다.
     *
     * @return
     */
    public List<Map<String, String>> getNewsChannels() {
        Guild guild = getJDA().getGuildById(GUILD_KEY);
        assert guild != null;

        List<NewsChannel> newsChannelList = guild.getNewsChannels();

        List<Map<String, String>> resultList = new ArrayList<>();

        for (NewsChannel channel : newsChannelList) {
            Map<String, String> newsChannel = new HashMap<>();
            newsChannel.put("id", channel.getId());
            newsChannel.put("name", channel.getName());
            resultList.add(newsChannel);
        }

        return resultList;
    }

    /**
     * 디스코드 길드 가입자들을 불러와 저장한다.
     *
     * @return
     * @throws InterruptedException
     */
    @Transactional
    public boolean memberRefresh() throws InterruptedException {
        JDA jda = getJDA();

        Guild guild = jda.getGuildById(GUILD_KEY);
        assert guild != null;

        List<Member> memberList = new ArrayList<>();
        guild.loadMembers().onSuccess(memberList::addAll);
        //3초 대기
        Thread.sleep(3000);

        //디스코드 전체 멤버 목록을 가져온다.
        if (memberList.size() > 0) {
            for (Member member : memberList) {
                if (discordMemberRepository.countByUserId(member.getUser().getId()) < 1) {
                    User user = member.getUser();
                    DiscordMember newMember = new DiscordMember();
                    newMember.setUserId(user.getId());
                    newMember.setUsername(user.getName());
                    newMember.setGlobalName(user.getGlobalName());
                    newMember.setNickname(member.getNickname());
                    newMember.setUserMension(user.getAsMention());
                    newMember.setIsDeleted(false);

                    //멤버 권한 저장
                    if (member.getRoles() != null && member.getRoles().size() > 0) {
                        Set<DiscordUserMension> discordUserMensionSet = new HashSet<>();
                        for (Role role : member.getRoles()) {
                            DiscordMention mention = discordMentionRepository.findByRoleId(role.getId()).orElse(null);
                            if (mention != null) {
                                DiscordUserMension discordUserMension = new DiscordUserMension();
                                discordUserMension.setDiscordMention(mention);
                                discordUserMension.setDiscordMember(newMember);
                                discordUserMensionRepository.save((discordUserMension));
                                discordUserMensionSet.add(discordUserMension);
                            }
                        }
                        if (discordUserMensionSet.size() > 0) {
                            newMember.setDiscordUserMensionSet(discordUserMensionSet);
                        }
                    }
                    discordMemberRepository.save(newMember);
                }//기존 회원 정보 최신화
                else {
                    DiscordMember discordMember = discordMemberRepository.findByUserId(member.getUser().getId()).orElse(null);
                    if (discordMember == null) {
                        continue;
                    }

                    User user = member.getUser();
                    discordMember.setUserId(user.getId());
                    discordMember.setUsername(user.getName());
                    discordMember.setNickname(member.getNickname());
                    discordMember.setGlobalName(user.getGlobalName());
                    discordMember.setUserMension(user.getAsMention());
                    discordMember.setIsDeleted(false);

                    //멤버 권한 저장
                    if (member.getRoles() != null && member.getRoles().size() > 0) {
                        Set<DiscordUserMension> discordUserMensionSet = discordUserMensionRepository.findAllByDiscordMember_Id(discordMember.getId());
                        for (Role role : member.getRoles()) {
                            DiscordMention mention = discordMentionRepository.findByRoleId(role.getId()).orElse(null);
                            if (mention != null) {
                                if (!ObjectUtils.isEmpty(discordUserMensionSet)) {
                                    //추가 권한을 부여했을 경우
                                    if (discordUserMensionSet.stream().noneMatch(v -> v.getDiscordMention().getId().equals(mention.getId()))) {
                                        DiscordUserMension discordUserMension = new DiscordUserMension();
                                        discordUserMension.setDiscordMention(mention);
                                        discordUserMension.setDiscordMember(discordMember);
                                        discordUserMensionRepository.save((discordUserMension));
                                        discordUserMensionSet.add(discordUserMension);
                                    }
                                } else {
                                    DiscordUserMension discordUserMension = new DiscordUserMension();
                                    discordUserMension.setDiscordMention(mention);
                                    discordUserMension.setDiscordMember(discordMember);
                                    discordUserMensionRepository.save((discordUserMension));
                                    discordUserMensionSet.add(discordUserMension);
                                }
                            }
                        }

                        //기존 권한 중에 삭제된게 있다면 삭제처리
                        List<DiscordUserMension> nonmatchMension = discordUserMensionSet.stream().filter(v -> member.getRoles().stream().noneMatch(r -> r.getId().equals(v.getDiscordMention().getRoleId()))).toList();
                        if (nonmatchMension != null && nonmatchMension.size() > 0) {
                            discordUserMensionRepository.deleteAll(nonmatchMension);
                        }

                        if (discordUserMensionSet.size() > 0) {
                            discordMember.setDiscordUserMensionSet(discordUserMensionSet);
                        }
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 디스코드 역할 멘션들을 불러와 저장한다.
     *
     * @return
     */
    @Transactional
    public boolean rolesRefresh() {
        JDA jda = getJDA();

        Guild guild = jda.getGuildById(GUILD_KEY);
        assert guild != null;

        List<Role> roleList = guild.getRoles();

        if (roleList.size() > 0) {
            for (Role role : roleList) {
                DiscordMention mention = discordMentionRepository.findByRoleId(role.getId()).orElse(null);
                if (mention == null) {
                    mention = new DiscordMention();
                    mention.setRoleId(role.getId());
                    mention.setRoleName(role.getName());
                    mention.setMention(role.getAsMention());
                    discordMentionRepository.save(mention);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 디스코드 길드의 역할들을 조회한다.
     *
     * @return
     */
    public List<DiscordMentionDto.Response> getMentions() {
        ChangSolJpaRestriction rs = new ChangSolJpaRestriction();
        return discordMentionRepository.findAll(Sort.by(Sort.Direction.ASC, "roleName")).stream().map(DiscordMention::toResponse).collect(Collectors.toList());
    }

    /**
     * 등록된 리그를 조회하여 오늘 시작하는 트랙 메세지를 푸시함.
     *
     * @param leagueId
     * @return
     */
    public boolean leagueMessagePush(long leagueId) {
        //리그 정보 조회
        League league = leagueRepository.findById(leagueId).orElse(null);
        assert league != null;

        LeagueTrack leagueTrack = league.getLeagueTracks().stream().filter(t -> t.getTrackDate().isEqual(LocalDate.now())).findFirst().orElse(null);
        if (leagueTrack == null) {
            return false;
        }

        JDA jda = getJDA();
        Guild guild = jda.getGuildById(GUILD_KEY);
        assert guild != null;

        //공지 채널을 얻어옴
        NewsChannel newsChannel = guild.getNewsChannelById(league.getNoticeChannelId());
        assert newsChannel != null;

        MessageEmbed msg = createLeagueMessage(league, leagueTrack);

        List<Button> actionButtonList = new ArrayList<>();
        for (LeagueButton leagueButton : league.getLeagueButtons()) {
            if ("Primary".equals(leagueButton.getButtonType())) {
                actionButtonList.add(Button.primary(String.valueOf(leagueButton.getId()), leagueButton.getButtonName()));
            } else if ("Success".equals(leagueButton.getButtonType())) {
                actionButtonList.add(Button.success(String.valueOf(leagueButton.getId()), leagueButton.getButtonName()));
            } else if ("Secondary".equals(leagueButton.getButtonType())) {
                actionButtonList.add(Button.secondary(String.valueOf(leagueButton.getId()), leagueButton.getButtonName()));
            } else {
                actionButtonList.add(Button.danger(String.valueOf(leagueButton.getId()), leagueButton.getButtonName()));
            }
        }
        defaultButtonAppender(actionButtonList);

        String alertMentions = "";
        for (LeagueDiscordMention joinAceptMention : league.getJoinAceptMentions()) {
            if ("".equals(alertMentions)) {
                alertMentions = joinAceptMention.getDiscordMention().getMention();
            } else {
                alertMentions += " " + joinAceptMention.getDiscordMention().getMention();
            }
        }

        String content = alertMentions;
        content += "\n**[" + leagueTrack.getTrackDate() + "]** 오늘 리그는 " + leagueTrack.getTrackCode().getCodeLabel() + " 입니다.";
        content += "\n시작 시간은 **" + league.getLeagueTime() + "**입니다.";

        MessageCreateData msgData = new MessageCreateBuilder()
                .addEmbeds(msg)
                .addContent(content)
//                .addContent(alertMentions)
//                .addContent(alertMentions + "\n**[" + leagueTrack.getTrackDate() + "]** 오늘 리그는 " + leagueTrack.getTrackCode().getCodeLabel() + " 입니다.")
//                .addContent("\n시작 시간은 **" + league.getLeagueTime() + "**입니다.")
                .addActionRow(actionButtonList)
                .build();

        newsChannel.sendMessage(msgData).queue();
        return true;
    }

    /**
     * 임베디드 메세지 생성
     *
     * @param league
     * @return
     */
    public MessageEmbed createLeagueMessage(League league, LeagueTrack leagueTrack) {
        //이벤트를 수정할 새로운 임베디드를 생성
        EmbedBuilder eb = new EmbedBuilder();

        //제목
        eb.setTitle(league.getTitle());
        //설명
        eb.appendDescription(league.getDescription());

        //내용 필드 추가
        //inline true 면 세로로 다단, false면 가로로 나뉨
        if (!ObjectUtils.isEmpty(league.getLeagueButtons())) {
            //참여자목록
            for (LeagueButton leagueButton : league.getLeagueButtons()) {
                List<LeagueTrackMember> regueTrackMemberList = leagueTrackMemberRepository.findAllByLeagueTrack_IdAndLeagueButton_IdOrderByCreatedDateTimeAscUpdatedDateTimeAsc(leagueTrack.getId(), leagueButton.getId());

                String joinMembers = "";
                for (LeagueTrackMember trackMember : regueTrackMemberList) {
                    //길드 닉네임이 있다면 1순위, 아니면 전체 닉네임 2순위, 아니면 기본 닉네임(아이디) 3순위
                    String userName = "";
                    if (!ObjectUtils.isEmpty(trackMember.getDiscordMember().getNickname())) {
                        userName = trackMember.getDiscordMember().getNickname();
                    } else if (!ObjectUtils.isEmpty(trackMember.getDiscordMember().getGlobalName())) {
                        userName = trackMember.getDiscordMember().getGlobalName();
                    } else {
                        userName = trackMember.getDiscordMember().getUsername();
                    }

                    if ("".equals(joinMembers)) {
                        joinMembers = userName;
                    } else {
                        joinMembers += "\n" + userName;
                    }
                }
                eb.addField(String.format("%s(%d/%d)", leagueButton.getButtonName(), regueTrackMemberList.size(), league.getJoinMemberLimit()), joinMembers, true);
            }

            //대기자 존재 유무 확인
            boolean isWaiting = leagueTrackWaitRepository.countByLeagueTrack_Id(leagueTrack.getId()) > 0;
            if(isWaiting) {
                boolean isFirst = true;
                for (LeagueButton leagueButton : league.getLeagueButtons()) {
                    List<LeagueTrackWait> waitList = leagueTrackWaitRepository.findAllByLeagueTrack_IdAndLeagueButton_IdOrderByCreatedDateTimeAsc(leagueTrack.getId(), leagueButton.getId());

                    if(!ObjectUtils.isEmpty(waitList)) {
                        String waitMembers = "";
                        for (LeagueTrackWait leagueTrackWait : waitList) {
                            //길드 닉네임이 있다면 1순위, 아니면 전체 닉네임 2순위, 아니면 기본 닉네임(아이디) 3순위
                            String userName = "";
                            if (!ObjectUtils.isEmpty(leagueTrackWait.getDiscordMember().getNickname())) {
                                userName = leagueTrackWait.getDiscordMember().getNickname();
                            } else if (!ObjectUtils.isEmpty(leagueTrackWait.getDiscordMember().getGlobalName())) {
                                userName = leagueTrackWait.getDiscordMember().getGlobalName();
                            } else {
                                userName = leagueTrackWait.getDiscordMember().getUsername();
                            }

                            if ("".equals(waitMembers)) {
                                waitMembers = userName;
                            } else {
                                waitMembers += "\n" + userName;
                            }
                        }

                        //처음 추가일 경우 세로로 처리
                        if (isFirst) {
                            isFirst = false;
                            eb.addField(String.format("%s 대기열(%d)", leagueButton.getButtonName(), waitList.size()), waitMembers, false);
                        } else {
                            eb.addField(String.format("%s 대기열(%d)", leagueButton.getButtonName(), waitList.size()), waitMembers, true);
                        }
                    }
                }
            }
        }

        //임베디드 존 좌측 컬러
        Color color;
        if ("red".equals(league.getColor())) {
            color = Color.red;
        } else if ("blue".equals(league.getColor())) {
            color = Color.blue;
        } else if ("yellow".equals(league.getColor())) {
            color = Color.yellow;
        } else if ("green".equals(league.getColor())) {
            color = Color.green;
        } else if ("white".equals(league.getColor())) {
            color = Color.white;
        } else {
            color = Color.magenta;
        }
        eb.setColor(color);

        //최하단 시간
        eb.setTimestamp(LocalDateTime.now().atZone(ZoneId.of("Asia/Tokyo")));

        //최하단 설명
        eb.setFooter(leagueTrack.getId().toString());

        //하단 이미지
        if (!ObjectUtils.isEmpty(league.getAttachFileGroup())) {
            if (!ObjectUtils.isEmpty(league.getAttachFileGroup().getAttachFileList())) {
                AttachFile file = league.getAttachFileGroup().getAttachFileList().get(0);
                String filePath = "/api/attach/get-image/";
                eb.setImage(SITE_URI + filePath + file.getFileName());
            }
        }

        return eb.build();
    }

    /**
     * 버튼 클릭 액션
     *
     * @param event
     */
    @Transactional
    public void embedButtonAction(ButtonInteractionEvent event) {
        if ("league-refresh".equals(event.getButton().getId())) {
            if (leagueManagerAuthCheck(event)) {
                leagueMessageRefresh(event);
            }
            return;
        } else if ("league-close".equals(event.getButton().getId())) {
            if (leagueManagerAuthCheck(event)) {
                leagueCloseAction(event);
            }
            return;
        }

        //이벤트 액션에따라 참여 목록을 저장 or 삭제한다.
        //리그트랙 번호는 임베디드 푸터에서 얻어와 불러온다.
        MessageEmbed embed = event.getMessage().getEmbeds().get(0); //임베디드는 1개만 생성함.

        //리그정보를 불러온다.
        assert Objects.requireNonNull(embed.getFooter()).getText() != null;
        long leagueTrackId = Long.parseLong(embed.getFooter().getText());

        LeagueTrack leagueTrack = leagueTrackRepository.findById(leagueTrackId).orElse(null);
        assert leagueTrack != null;

        //마감됐다면
        if (leagueTrack.getIsColsed()) {
            userDmSend(event, leagueTrack.getLeague().getLeagueName() + " 리그는 마감됐습니다.");
            return;
        }

        League league = leagueTrack.getLeague();
        DiscordMember discordMember = discordMemberRepository.findByUserId(Objects.requireNonNull(event.getMember()).getUser().getId()).orElse(null);

        //멤버 등록이 안된 경우
        if (discordMember == null) {
            log.error("discordMember is not found!!!");
            //관리자에게 문의 메세지 전송
            userDmSend(event, "시스템에 멤버로 등록되지 않았습니다. 관리자에게 문의해주세요.");
            return;
        }

        //리그 참여자 권한 체크
        boolean isJoinAuth = false;
        for (DiscordUserMension discordUserMension : discordMember.getDiscordUserMensionSet()) {
            if (league.getJoinAceptMentions().stream().anyMatch(m -> m.getDiscordMention().equals(discordUserMension.getDiscordMention()))) {
                isJoinAuth = true;
                break;
            }
        }

        //참여 대상 권한이 아닐 경우 DM발송
        if (!isJoinAuth) {
            userDmSend(event, league.getLeagueName() + " 참여 대상이 아닙니다.");
            return;
        }

        //리그 참여 처리
        boolean isSuccess = leagueJoinProc(event, leagueTrack, discordMember);

        //메세지 처리
        if(isSuccess) {
            editMessageSend(event, leagueTrack, leagueTrack.getIsColsed());
        }
    }

    /**
     * 참여 취소 모달 생성
     * @param messageId
     * @param leagueTrack
     * @return
     */
    public Modal createJoinCancelModal(String messageId, LeagueTrack leagueTrack){
        TextInput field1 = TextInput.create("field1", "리그명", TextInputStyle.SHORT)
                .setPlaceholder(leagueTrack.getLeague().getLeagueName())
                .setMinLength(leagueTrack.getLeague().getLeagueName().length())
                .setMaxLength(leagueTrack.getLeague().getLeagueName().length())
                .setValue(leagueTrack.getLeague().getLeagueName())
                .setRequired(true)
                .build();

        TextInput field2 = TextInput.create("field2", "리그번호", TextInputStyle.SHORT)
                .setPlaceholder(leagueTrack.getId().toString())
                .setMinLength(leagueTrack.getId().toString().length())
                .setMaxLength(leagueTrack.getId().toString().length())
                .setValue(leagueTrack.getId().toString())
                .setRequired(true)
                .build();

        TextInput field3 = TextInput.create("joinCancelField", "참여취소", TextInputStyle.SHORT)
                .setPlaceholder("참여취소")
                .setMinLength(4)
                .setMaxLength(4)
                .setValue("참여취소")
                .setRequired(true)
                .build();

        return Modal.create(messageId, "참여취소")
//                .addActionRow(field1)
//                .addActionRow(field2)
                .addActionRow(field3)
                .build();
    }

    /**
     * 모달 액션
     * @param event
     */
    @Transactional
    public void modalInteraction(@NotNull ModalInteractionEvent event){
        String modalId = event.getModalId();
        String messageId = modalId.split("\\|")[0];
        String reagueTrackId = modalId.split("\\|")[1];

        log.info("modalId :::: " + modalId);

        LeagueTrack leagueTrack = leagueTrackRepository.findById(Long.parseLong(reagueTrackId)).orElse(null);
        if(leagueTrack == null){
            event.reply("리그 정보가 존재하지 않습니다.\n관리자에게 문의해 주세요").queue();
            return;
        }

        DiscordMember discordMember = discordMemberRepository.findByUserId(Objects.requireNonNull(event.getMember()).getUser().getId()).orElse(null);
        if(discordMember == null ){
            event.reply("멤버 정보가 존재하지 않습니다.\n관리자에게 문의해 주세요").queue();
            return;
        }

        String nickName = "";
        if(!ObjectUtils.isEmpty(discordMember.getNickname())){
            nickName = discordMember.getNickname();
        }else if(!ObjectUtils.isEmpty(discordMember.getGlobalName())){
            nickName = discordMember.getGlobalName();
        }else{
            nickName = discordMember.getUsername();
        }

        //입력값 검증
        if(!"참여취소".equals(Objects.requireNonNull(event.getValue("joinCancelField")).getAsString())){
            event.reply(nickName + "님 **[참여취소]** 입력 값이 잘못되었습니다.").queue();
            return;
        }

        //공지 채널
        NewsChannel channel = getJDA().getNewsChannelById(event.getChannelIdLong());
        assert channel != null;

        //기존 참가자 취소처리
        LeagueTrackMember regueTrackMember = leagueTrackMemberRepository.findByDiscordMember_UserIdAndLeagueTrack_Id(discordMember.getUserId(), leagueTrack.getId()).orElse(null);
        if(regueTrackMember == null){
            event.reply(nickName + "님 리그 참가 정보가 존재하지 않습니다.").queue();
            return;
        }

        String buttonLabel = regueTrackMember.getLeagueButton().getButtonName();
        leagueTrackMemberRepository.delete(regueTrackMember);
        leagueTrackMemberRepository.flush();

        //대기자가 있을 경우 참여자로 변경 처리
        //카테고리가 변경되었기 때문에 전체 카테고리 대기열을 순회하여 대기열처리해줌.
        for (LeagueButton leagueButton : leagueTrack.getLeague().getLeagueButtons()) {
            changeLeagueTrackWaiterToLeagueTrackMember(event, leagueTrack, leagueButton);
        }

        //메세지 찾아와서 변경하기
        channel.retrieveMessageById(messageId).queue((message) -> {
            MessageEmbed embed = message.getEmbeds().get(0);

            //변경사항 반영
            channel.editMessageById(messageId,eidtLeagueMessage(leagueTrack, false, embed)).queue();
        });

        //처리 응답(생략 불가능)
        event.reply(nickName + "님 **" + buttonLabel + "** 취소 처리 되었습니다.").queue();
    }

    /**
     * embed 수정 메세지 생성 및 전송처리
     * @param event
     * @param leagueTrack
     * @param isClosed
     */
    public void editMessageSend(ButtonInteractionEvent event, LeagueTrack leagueTrack, boolean isClosed) {
        League league = leagueTrack.getLeague();

        MessageEmbed embed = event.getMessage().getEmbeds().get(0);

        //메세지 수정 발송
        event.editMessage(eidtLeagueMessage(leagueTrack, isClosed, embed)).queue();
    }

    /**
     * embed 수정 메새지 생성
     * @param leagueTrack
     * @param isClosed
     * @param embed
     * @return
     */
    @Transactional
    public MessageEditData eidtLeagueMessage(LeagueTrack leagueTrack, boolean isClosed, MessageEmbed embed){
        League league = leagueTrack.getLeague();

        //이벤트 메세지로부터 임베디드 메세지를 받아와 필스를 수정한다.
        EmbedBuilder embedBuilder = new EmbedBuilder(embed);
        embedBuilder.clearFields();

        //마감일 경우 타이틀에 마감 표기함
        if (isClosed) {
            embedBuilder.setTitle(league.getTitle() + "[마감]");
        } else {
            embedBuilder.setTitle(league.getTitle());
        }

        long leagueTrackId = Long.parseLong(embed.getFooter().getText());

        //카테고리 데이터 생성
        for (LeagueButton leagueButton : league.getLeagueButtons()) {
            List<LeagueTrackMember> regueTrackMemberList = leagueTrackMemberRepository.findAllByLeagueTrack_IdAndLeagueButton_IdOrderByCreatedDateTimeAscUpdatedDateTimeAsc(leagueTrackId, leagueButton.getId());

            String joinMembers = "";
            for (LeagueTrackMember trackMember : regueTrackMemberList) {
                //길드 닉네임이 있다면 1순위, 아니면 전체 닉네임 2순위, 아니면 기본 닉네임(아이디) 3순위
                String userName = "";
                if (!ObjectUtils.isEmpty(trackMember.getDiscordMember().getNickname())) {
                    userName = trackMember.getDiscordMember().getNickname();
                } else if (!ObjectUtils.isEmpty(trackMember.getDiscordMember().getGlobalName())) {
                    userName = trackMember.getDiscordMember().getGlobalName();
                } else {
                    userName = trackMember.getDiscordMember().getUsername();
                }

                if ("".equals(joinMembers)) {
                    joinMembers = userName;
                } else {
                    joinMembers += "\n" + userName;
                }
            }

            embedBuilder.addField(String.format("%s(%d/%d)", leagueButton.getButtonName(), regueTrackMemberList.size(), league.getJoinMemberLimit()), joinMembers, true);
        }

        //대기자 존재 유무 확인
        boolean isWaiting = leagueTrackWaitRepository.countByLeagueTrack_Id(leagueTrackId) > 0;
        if(isWaiting) {
            boolean isFirst = true;
            for (LeagueButton leagueButton : league.getLeagueButtons()) {
                List<LeagueTrackWait> waitList = leagueTrackWaitRepository.findAllByLeagueTrack_IdAndLeagueButton_IdOrderByCreatedDateTimeAsc(leagueTrackId, leagueButton.getId());

                if(!ObjectUtils.isEmpty(waitList)) {
                    String waitMembers = "";
                    for (LeagueTrackWait leagueTrackWait : waitList) {
                        //길드 닉네임이 있다면 1순위, 아니면 전체 닉네임 2순위, 아니면 기본 닉네임(아이디) 3순위
                        String userName = "";
                        if (!ObjectUtils.isEmpty(leagueTrackWait.getDiscordMember().getNickname())) {
                            userName = leagueTrackWait.getDiscordMember().getNickname();
                        } else if (!ObjectUtils.isEmpty(leagueTrackWait.getDiscordMember().getGlobalName())) {
                            userName = leagueTrackWait.getDiscordMember().getGlobalName();
                        } else {
                            userName = leagueTrackWait.getDiscordMember().getUsername();
                        }

                        if ("".equals(waitMembers)) {
                            waitMembers = userName;
                        } else {
                            waitMembers += "\n" + userName;
                        }
                    }

                    //처음 추가일 경우 세로로 처리
                    if (isFirst) {
                        isFirst = false;
                        embedBuilder.addField(String.format("%s 대기열(%d)", leagueButton.getButtonName(), waitList.size()), waitMembers, false);
                    } else {
                        embedBuilder.addField(String.format("%s 대기열(%d)", leagueButton.getButtonName(), waitList.size()), waitMembers, false);
                    }
                }
            }
        }

        List<Button> actionButtonList = new ArrayList<>();
        for (LeagueButton leagueButton : league.getLeagueButtons()) {
            if ("Primary".equals(leagueButton.getButtonType())) {
                actionButtonList.add(Button.primary(String.valueOf(leagueButton.getId()), leagueButton.getButtonName()));
            } else if ("Success".equals(leagueButton.getButtonType())) {
                actionButtonList.add(Button.success(String.valueOf(leagueButton.getId()), leagueButton.getButtonName()));
            } else if ("Secondary".equals(leagueButton.getButtonType())) {
                actionButtonList.add(Button.secondary(String.valueOf(leagueButton.getId()), leagueButton.getButtonName()));
            } else {
                actionButtonList.add(Button.danger(String.valueOf(leagueButton.getId()), leagueButton.getButtonName()));
            }
        }
        //기본 버튼 새팅(새로고침, 마감)
        defaultButtonAppender(actionButtonList);

        String alertMentions = "";
        for (LeagueDiscordMention joinAceptMention : league.getJoinAceptMentions()) {
            if ("".equals(alertMentions)) {
                alertMentions = joinAceptMention.getDiscordMention().getMention();
            } else {
                alertMentions += " " + joinAceptMention.getDiscordMention().getMention();
            }
        }

        String content = alertMentions;
        content += "\n**[" + leagueTrack.getTrackDate() + "]** 오늘 리그는 " + leagueTrack.getTrackCode().getCodeLabel() + " 입니다.";
        content += "\n시작 시간은 **" + league.getLeagueTime() + "**입니다.";

        //수정 메세지 세팅
        MessageEditData messageEditData = new MessageEditBuilder()
                .setEmbeds(embedBuilder.build())
                .setActionRow(actionButtonList)
                .setContent(content)
                .build();

        return messageEditData;
    }

    /**
     * 유저에게 dm 알림
     *
     * @param event
     * @param text
     */
    public void userDmSend(ButtonInteractionEvent event, String text) {
        if (event == null || ObjectUtils.isEmpty(text)) {
            return;
        }

        event.getMember()
                .getUser()
                .openPrivateChannel()
                .queue(
                        channel -> {
                            channel.sendMessage(text).queue();
                        });
    }

    /**
     * 특정 유저에서 dm 알림
     * @param event
     * @param text
     * @param userId
     */
    public void userDmSendByUserId(ButtonInteractionEvent event, String text, String userId){
        if (event == null || ObjectUtils.isEmpty(text)) {
            return;
        }

        getJDA().getGuildById(event.getGuild().getId())
                .getMemberById(userId)
                .getUser()
                .openPrivateChannel()
                .queue(
                    channel -> {
                        channel.sendMessage(text).queue();
                    });
    }

    /**
     * 특정 유저에서 dm 알림
     * @param event
     * @param text
     * @param userId
     */
    public void userDmSendByUserId(ModalInteractionEvent event, String text, String userId){
        if (event == null || ObjectUtils.isEmpty(text)) {
            return;
        }

        getJDA().getGuildById(event.getGuild().getId())
                .getMemberById(userId)
                .getUser()
                .openPrivateChannel()
                .queue(
                        channel -> {
                            channel.sendMessage(text).queue();
                        });
    }

    /**
     * 오늘 시작되는 리그가 있는지 카운트를 조회한다.
     *
     * @return
     */
    public long countLeagueTrackStartToday() {
        return leagueTrackRepository.countAllByTrackDateAndLeague_IsDeleted(LocalDate.now(), false);
    }

    /**
     * 오늘 시작되는 리그트랙 목록을 조회한다.
     *
     * @return
     */
    public List<LeagueTrack> getLeagueTrackStartToday() {
        return leagueTrackRepository.findAllByTrackDateAndLeague_IsDeleted(LocalDate.now(), false);
    }

    /**
     * 리그 기본버튼 추가
     * @param actionButtonList
     */
    public void defaultButtonAppender(List<Button> actionButtonList) {
        actionButtonList.add(Button.secondary("league-refresh", "새로고침"));
        actionButtonList.add(Button.danger("league-close", "마감"));
    }

    /**
     * 리그 메세지 새로고침
     * @param event
     */
    public void leagueMessageRefresh(ButtonInteractionEvent event) {
        MessageEmbed embed = event.getMessage().getEmbeds().get(0); //임베디드는 1개만 생성함.

        //리그정보를 불러온다.
        assert Objects.requireNonNull(embed.getFooter()).getText() != null;
        long leagueTrackId = Long.parseLong(embed.getFooter().getText());

        LeagueTrack leagueTrack = leagueTrackRepository.findById(leagueTrackId).orElse(null);
        assert leagueTrack != null;

        editMessageSend(event, leagueTrack, leagueTrack.getIsColsed());
    }

    /**
     * 리그 참가 신청 마감/마감해제
     * @param event
     */
    @Transactional
    public void leagueCloseAction(ButtonInteractionEvent event) {
        MessageEmbed embed = event.getMessage().getEmbeds().get(0); //임베디드는 1개만 생성함.

        //리그정보를 불러온다.
        assert Objects.requireNonNull(embed.getFooter()).getText() != null;
        long leagueTrackId = Long.parseLong(embed.getFooter().getText());

        LeagueTrack leagueTrack = leagueTrackRepository.findById(leagueTrackId).orElse(null);
        assert leagueTrack != null;

        if (leagueTrack.getIsColsed()) {
            leagueTrack.setIsColsed(false);
        } else {
            leagueTrack.setIsColsed(true);
        }

        editMessageSend(event, leagueTrack, leagueTrack.getIsColsed());
    }

    /**
     * 운영진, 리그운영진 권한 체크
     *
     * @param event
     * @return
     */
    public boolean leagueManagerAuthCheck(ButtonInteractionEvent event) {
        DiscordMember discordMember = discordMemberRepository.findByUserId(Objects.requireNonNull(event.getMember()).getUser().getId()).orElse(null);
        return discordMember.getDiscordUserMensionSet().stream().anyMatch(r -> "1125386665574273024".equals(r.getDiscordMention().getRoleId()) || "1125983811797270548".equals(r.getDiscordMention().getRoleId()));
    }

    /**
     * 리그 참여 처리
     */
    @Transactional
    public boolean leagueJoinProc(ButtonInteractionEvent event, LeagueTrack leagueTrack, DiscordMember discordMember) {
        LeagueTrackMember regueTrackMember = leagueTrackMemberRepository.findByDiscordMember_UserIdAndLeagueTrack_Id(discordMember.getUserId(), leagueTrack.getId()).orElse(null);
        League league = leagueTrack.getLeague();

        //현재 참여가 안된 경우 참여 등록처리
        LeagueButton joinButton = league.getLeagueButtons().stream().filter(v -> Objects.equals(event.getButton().getId(), v.getId().toString())).findFirst().orElse(null);
        if (joinButton == null) {
            log.error("LeagueButton is not found!!!");
            userDmSend(event, "처리 오류가 발생하였습니다. 관리자에게 문의해주세요.");
            return false;
        }

        //신규 참여자 or 대기열참여자
        if (regueTrackMember == null) {
            //대기열 확인
            LeagueTrackWait leagueTrackWait = leagueTrackWaitRepository.findByLeagueTrack_IdAndDiscordMember_Id(leagueTrack.getId(), discordMember.getId()).orElse(null);

            //신규 참여자일 경우
            if(leagueTrackWait == null) {
                long joinCnt = leagueTrackMemberRepository.countByLeagueTrack_IdAndLeagueButton_Id(leagueTrack.getId(), joinButton.getId());
                if (joinCnt >= league.getJoinMemberLimit()) {
                    //대기자 로직으로 변경
//                userDmSend(event, "참여 가능 인원이 초과하였습니다.");
                    appendLeagueTrackWait(discordMember, leagueTrack, joinButton);

                    editMessageSend(event, leagueTrack, leagueTrack.getIsColsed());
                    return false;
                }

                LeagueTrackMember newRegueTrackMember = new LeagueTrackMember();
                newRegueTrackMember.setLeagueButton(joinButton);
                newRegueTrackMember.setLeagueTrack(leagueTrack);
                newRegueTrackMember.setDiscordMember(discordMember);
                leagueTrackMemberRepository.save(newRegueTrackMember);
            }//대기열 참여자일 경우 
            else{
                //동일한 카테고리일 경우 삭제 처리
                if(Objects.equals(joinButton, leagueTrackWait.getLeagueButton())){
                    leagueTrackWaitRepository.delete(leagueTrackWait);
                }//다른 카테고리에서 변경한 경우
                else{
                    //참여가능 인원 확인
                    long joinCnt = leagueTrackMemberRepository.countByLeagueTrack_IdAndLeagueButton_Id(leagueTrack.getId(), joinButton.getId());
                    //참여 가능 인원 초과한 경우 대기열 변경 등록
                    if (joinCnt >= league.getJoinMemberLimit()) {
                        leagueTrackWaitRepository.delete(leagueTrackWait);
                        leagueTrackWaitRepository.flush();
                        appendLeagueTrackWait(discordMember, leagueTrack, joinButton);
                    }//참여가능할 경우 대기열에서 제거하고 참여자로 등록
                    else{
                        LeagueTrackMember newRegueTrackMember = new LeagueTrackMember();
                        newRegueTrackMember.setLeagueButton(joinButton);
                        newRegueTrackMember.setLeagueTrack(leagueTrack);
                        newRegueTrackMember.setDiscordMember(discordMember);
                        leagueTrackMemberRepository.save(newRegueTrackMember);
                        leagueTrackWaitRepository.delete(leagueTrackWait);
                    }
                }
            }
        }//현재 참여중인 경우 삭제처리
        else {
            //현재 참여 타입과 동일한 타입으로 눌렀을 경우 참여 취소 처리
            if (joinButton.getId().equals(regueTrackMember.getLeagueButton().getId())) {
//                leagueTrackMemberRepository.delete(regueTrackMember);
//                leagueTrackMemberRepository.flush();
                //취소할건지 모달 팝업
                event.replyModal(createJoinCancelModal(event.getMessageId() + "|" + leagueTrack.getId(), leagueTrack)).queue();
                return false;
                //대기자가 있을 경우 참여자로 변경 처리
                //카테고리가 변경되었기 때문에 전체 카테고리 대기열을 순회하여 대기열처리해줌.
//                for (LeagueButton leagueButton : leagueTrack.getLeague().getLeagueButtons()) {
//                    changeLeagueTrackWaiterToLeagueTrackMember(event, leagueTrack, leagueButton);
//                }
            }//현재 참여 타입과 다른 타입으로 신청한 경우 변경처리
            else {
                //참여인원 제한 체크
                long joinCnt = leagueTrackMemberRepository.countByLeagueTrack_IdAndLeagueButton_Id(leagueTrack.getId(), joinButton.getId());
                if (joinCnt >= league.getJoinMemberLimit()) {
                    leagueTrackMemberRepository.delete(regueTrackMember);

                    //대기자 로직으로 변경
//                    userDmSend(event, "참여 가능 인원이 초과하였습니다.");
                    appendLeagueTrackWait(discordMember, leagueTrack, joinButton);
                    //카테고리가 변경되었기 때문에 전체 카테고리 대기열을 순회하여 대기열처리해줌.
                    for (LeagueButton leagueButton : leagueTrack.getLeague().getLeagueButtons()) {
                        changeLeagueTrackWaiterToLeagueTrackMember(event, leagueTrack, leagueButton);
                    }

                    editMessageSend(event, leagueTrack, leagueTrack.getIsColsed());
                    return false;
                } else {
                    regueTrackMember.setLeagueButton(joinButton);
                    //카테고리가 변경되었기 때문에 전체 카테고리 대기열을 순회하여 대기열처리해줌.
                    for (LeagueButton leagueButton : leagueTrack.getLeague().getLeagueButtons()) {
                        changeLeagueTrackWaiterToLeagueTrackMember(event, leagueTrack, leagueButton);
                    }
                }
            }
        }
        return true;
    }

    /**
     * 리그 대기자 추가처리
     *
     * @param discordMember
     * @param leagueTrack
     * @param leagueButton
     */
    @Transactional
    public void appendLeagueTrackWait(DiscordMember discordMember, LeagueTrack leagueTrack, LeagueButton leagueButton) {
        LeagueTrackWait leagueTrackWait = new LeagueTrackWait();
        leagueTrackWait.setDiscordMember(discordMember);
        leagueTrackWait.setLeagueTrack(leagueTrack);
        leagueTrackWait.setLeagueButton(leagueButton);
        leagueTrackWaitRepository.save(leagueTrackWait);
    }

    /**
     * 참석 대기자 취소 시 대기열 변경처리
     * @param event
     * @param leagueTrack
     * @param leagueButton
     */
    @Transactional
    public void changeLeagueTrackWaiterToLeagueTrackMember(ButtonInteractionEvent event, LeagueTrack leagueTrack, LeagueButton leagueButton) {
        //대기자 존재 유무 확인
        boolean isWaiting = leagueTrackWaitRepository.countByLeagueTrack_IdAndLeagueButton_Id(leagueTrack.getId(), leagueButton.getId()) > 0;

        //대기자가 있을 경우
        if (isWaiting) {
            //현재 참여자 카운트 확인(동시 작동시 초과되는거 방지)
            long joinCnt = leagueTrackMemberRepository.countByLeagueTrack_IdAndLeagueButton_Id(leagueTrack.getId(), leagueButton.getId());
            if(joinCnt >= leagueTrack.getLeague().getJoinMemberLimit()){
                return;
            }

            //대기자에서 제거
            LeagueTrackWait leagueTrackWait = leagueTrackWaitRepository.findTop1ByLeagueTrack_IdAndLeagueButton_IdOrderByCreatedDateTimeAsc(leagueTrack.getId(), leagueButton.getId()).orElse(null);
            if (leagueTrackWait != null) {
                //대기열 참석자에게 개인DM으로 알림
                String dmText = "참석자 취소로 " + leagueTrackWait.getDiscordMember().getUserMension() + " 님 참석으로 전환되었습니다.";
                userDmSendByUserId(event, dmText, leagueTrackWait.getDiscordMember().getUserId());

                //참여자로 추가
                LeagueTrackMember newRegueTrackMember = new LeagueTrackMember();
                newRegueTrackMember.setLeagueButton(leagueButton);
                newRegueTrackMember.setLeagueTrack(leagueTrack);
                newRegueTrackMember.setDiscordMember(leagueTrackWait.getDiscordMember());
                leagueTrackMemberRepository.save(newRegueTrackMember);

                //대기열에서 삭제
                leagueTrackWaitRepository.delete(leagueTrackWait);
                leagueTrackWaitRepository.flush();
            }else{
                log.error("대기자 처리 중 오류 발생!!!");
            }
        }
    }

    /**
     * 참석 대기자 취소 시 대기열 변경처리
     * @param event
     * @param leagueTrack
     * @param leagueButton
     */
    @Transactional
    public void changeLeagueTrackWaiterToLeagueTrackMember(ModalInteractionEvent event, LeagueTrack leagueTrack, LeagueButton leagueButton) {
        //대기자 존재 유무 확인
        boolean isWaiting = leagueTrackWaitRepository.countByLeagueTrack_IdAndLeagueButton_Id(leagueTrack.getId(), leagueButton.getId()) > 0;

        //대기자가 있을 경우
        if (isWaiting) {
            //현재 참여자 카운트 확인(동시 작동시 초과되는거 방지)
            long joinCnt = leagueTrackMemberRepository.countByLeagueTrack_IdAndLeagueButton_Id(leagueTrack.getId(), leagueButton.getId());
            if(joinCnt >= leagueTrack.getLeague().getJoinMemberLimit()){
                return;
            }

            //대기자에서 제거
            LeagueTrackWait leagueTrackWait = leagueTrackWaitRepository.findTop1ByLeagueTrack_IdAndLeagueButton_IdOrderByCreatedDateTimeAsc(leagueTrack.getId(), leagueButton.getId()).orElse(null);
            if (leagueTrackWait != null) {
                //대기열 참석자에게 개인DM으로 알림
                String dmText = "참석자 취소로 " + leagueTrackWait.getDiscordMember().getUserMension() + " 님 참석으로 전환되었습니다.";
                userDmSendByUserId(event, dmText, leagueTrackWait.getDiscordMember().getUserId());

                //참여자로 추가
                LeagueTrackMember newRegueTrackMember = new LeagueTrackMember();
                newRegueTrackMember.setLeagueButton(leagueButton);
                newRegueTrackMember.setLeagueTrack(leagueTrack);
                newRegueTrackMember.setDiscordMember(leagueTrackWait.getDiscordMember());
                leagueTrackMemberRepository.save(newRegueTrackMember);

                //대기열에서 삭제
                leagueTrackWaitRepository.delete(leagueTrackWait);
                leagueTrackWaitRepository.flush();
            }else{
                log.error("대기자 처리 중 오류 발생!!!");
            }
        }
    }
}
