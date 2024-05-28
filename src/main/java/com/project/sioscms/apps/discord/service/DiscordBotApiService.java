package com.project.sioscms.apps.discord.service;

import com.project.sioscms.SioscmsApplication;
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
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
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

    private final DiscordMemberRepository discordMemberRepository;
    private final DiscordMentionRepository discordMentionRepository;
    private final DiscordUserMensionRepository discordUserMensionRepository;
    private final ReagueRepository reagueRepository;
    private final ReagueTrackRepository reagueTrackRepository;
    private final RegueTrackMemberRepository regueTrackMemberRepository;

    private JDA getJDA(){
        if(SioscmsApplication.getJda() == null){
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
        }else {
            return SioscmsApplication.getJda();
        }
//        return null;
    }

    /**
     * 디스코드 길드 가입자 목록 조회
     * @return
     */
    public List<DiscordMemberDto.Response> getDiscordMembers(DiscordMemberDto.Request requestDto){
        ChangSolJpaRestriction restriction = new ChangSolJpaRestriction();
        restriction.equals("isDeleted", false);
        return discordMemberRepository.findAll(restriction.toSpecification(), Sort.by(Sort.Direction.DESC, "username"))
                .stream().map(DiscordMember::toResponse).collect(Collectors.toList());
    }

    /**
     * 디스코드 공지 채널 목록을 얻어온다.
     * @return
     */
    public List<Map<String, String>> getNewsChannels(){
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
        if(memberList.size() > 0){
            for (Member member : memberList) {
                if(discordMemberRepository.countByUserId(member.getUser().getId()) < 1){
                    User user = member.getUser();
                    DiscordMember newMember = new DiscordMember();
                    newMember.setUserId(user.getId());
                    newMember.setUsername(user.getName());
                    newMember.setGlobalName(user.getGlobalName());
                    newMember.setUserMension(user.getAsMention());
                    newMember.setIsDeleted(false);

                    //멤버 권한 저장
                    if(member.getRoles() != null && member.getRoles().size() > 0) {
                        Set<DiscordUserMension> discordUserMensionSet = new HashSet<>();
                        for (Role role : member.getRoles()) {
                            DiscordMention mention = discordMentionRepository.findByRoleId(role.getId()).orElse(null);
                            if(mention != null){
                                DiscordUserMension discordUserMension = new DiscordUserMension();
                                discordUserMension.setDiscordMention(mention);
                                discordUserMension.setDiscordMember(newMember);
                                discordUserMensionRepository.save((discordUserMension));
                                discordUserMensionSet.add(discordUserMension);
                            }
                        }
                        if(discordUserMensionSet.size() > 0) {
                            newMember.setDiscordUserMensionSet(discordUserMensionSet);
                        }
                    }
                    discordMemberRepository.save(newMember);
                }
            }
            return true;
        }else {
            return false;
        }
    }

    /**
     * 디스코드 역할 멘션들을 불러와 저장한다.
     * @return
     */
    @Transactional
    public boolean rolesRefresh(){
        JDA jda = getJDA();

        Guild guild = jda.getGuildById(GUILD_KEY);
        assert guild != null;

        List<Role> roleList = guild.getRoles();

        if(roleList.size() > 0) {
            for (Role role : roleList) {
                DiscordMention mention = new DiscordMention();
                mention.setRoleId(role.getId());
                mention.setRoleName(role.getName());
                mention.setMention(role.getAsMention());
                discordMentionRepository.save(mention);
            }
            return true;
        }else{
            return false;
        }
    }

    /**
     * 디스코드 길드의 역할들을 조회한다.
     * @return
     */
    public List<DiscordMentionDto.Response> getMentions(){
        ChangSolJpaRestriction rs = new ChangSolJpaRestriction();
        return discordMentionRepository.findAll(Sort.by(Sort.Direction.ASC, "roleName")).stream().map(DiscordMention::toResponse).collect(Collectors.toList());
    }

    /**
     * 등록된 리그를 조회하여 오늘 시작하는 트랙 메세지를 푸시함.
     * @param reagueId
     * @return
     */
    public boolean reagueMessagePush(long reagueId){
        //리그 정보 조회
        Reague reague = reagueRepository.findById(reagueId).orElse(null);
        assert reague != null;

        ReagueTrack reagueTrack = reague.getReagueTracks().stream().filter(t -> t.getTrackDate().isEqual(LocalDate.now())).findFirst().orElse(null);
        if(reagueTrack == null){
            return false;
        }

        JDA jda = getJDA();
        Guild guild = jda.getGuildById(GUILD_KEY);
        assert guild != null;

        //공지 채널을 얻어옴
        NewsChannel newsChannel = guild.getNewsChannelById(reague.getNoticeChannelId());
        assert newsChannel != null;

        MessageEmbed msg = createReagueMessage(reague, reagueTrack);

        List<Button> actionButtonList = new ArrayList<>();
        for (ReagueButton reagueButton : reague.getReagueButtons()) {
            if("Primary".equals(reagueButton.getButtonType())){
                actionButtonList.add(Button.primary(String.valueOf(reagueButton.getId()), reagueButton.getButtonName()));
            }else if("Success".equals(reagueButton.getButtonType())){
                actionButtonList.add(Button.success(String.valueOf(reagueButton.getId()), reagueButton.getButtonName()));
            }else if("Secondary".equals(reagueButton.getButtonType())){
                actionButtonList.add(Button.secondary(String.valueOf(reagueButton.getId()), reagueButton.getButtonName()));
            }else{
                actionButtonList.add(Button.danger(String.valueOf(reagueButton.getId()), reagueButton.getButtonName()));
            }
        }

        MessageCreateData msgData = new MessageCreateBuilder()
                .addEmbeds(msg)
                .addContent(reagueTrack.getTrackDate() + " 오늘 리그는 " + reagueTrack.getTrackCode().getCodeLabel() + " 입니다.")
                .addActionRow(actionButtonList)
                .build();

        newsChannel.sendMessage(msgData).queue();
        return true;
    }

    /**
     * 임베디드 메세지 생성
     * @param reague
     * @return
     */
    public MessageEmbed createReagueMessage(Reague reague, ReagueTrack reagueTrack){
        //이벤트를 수정할 새로운 임베디드를 생성
        EmbedBuilder eb = new EmbedBuilder();

        //제목
        eb.setTitle(reague.getTitle());
        //설명
        eb.appendDescription(reague.getDescription());

        //내용 필드 추가
        //inline true 면 세로로 다단, false면 가로로 나뉨
        if(!ObjectUtils.isEmpty(reague.getReagueButtons())){
            for (ReagueButton reagueButton : reague.getReagueButtons()) {
                List<RegueTrackMember> regueTrackMemberList = regueTrackMemberRepository.findAllByReagueTrack_IdAndJoinType(reagueTrack.getId(), reagueButton.getButtonType());

                String joinMembers = "";
                for (RegueTrackMember trackMember : regueTrackMemberList) {
                    String userName = ObjectUtils.isEmpty(trackMember.getDiscordMember().getGlobalName())? trackMember.getDiscordMember().getUsername():trackMember.getDiscordMember().getGlobalName();
                    if("".equals(joinMembers)){
                        joinMembers = userName;
                    }else {
                        joinMembers += "\n" + userName;
                    }
                }
                eb.addField(String.format("%s(%d/%d)",reagueButton.getButtonName(), regueTrackMemberList.size(), reague.getJoinMemberLimit()), joinMembers, true);
            }
        }

        //하단 이미지
        if(!ObjectUtils.isEmpty(reague.getAttachFileGroup())) {
            if(!ObjectUtils.isEmpty(reague.getAttachFileGroup().getAttachFileList())) {
                eb.setImage(SITE_URI + "/api/attach/get-image/" + reague.getAttachFileGroup().getAttachFileList().get(0).getFileName());
            }
        }

        //임베디드 존 좌측 컬러
        Color color;
        if("red".equals(reague.getColor())){
            color = Color.red;
        }else if("blue".equals(reague.getColor())){
            color = Color.blue;
        }else if("yellow".equals(reague.getColor())){
            color = Color.yellow;
        }else if("green".equals(reague.getColor())){
            color = Color.green;
        }else if("white".equals(reague.getColor())){
            color = Color.white;
        }else {
            color = Color.magenta;
        }
        eb.setColor(color);

        //최하단 시간
        eb.setTimestamp(LocalDateTime.now().atZone(ZoneId.of("Asia/Tokyo")));

        //최하단 설명
        eb.setFooter(reagueTrack.getId().toString());

        return eb.build();
    }

    /**
     * 버튼 클릭 액션
     * @param event
     */
    @Transactional
    public void embedButtonAction(ButtonInteractionEvent event){
        //이벤트 액션에따라 참여 목록을 저장 or 삭제한다.
        //리그트랙 번호는 임베디드 푸터에서 얻어와 불러온다.
        MessageEmbed embed = event.getMessage().getEmbeds().get(0); //임베디드는 1개만 생성함.

        //리그정보를 불러온다.
        assert Objects.requireNonNull(embed.getFooter()).getText() != null;
        long reagueTrackId = Long.parseLong(embed.getFooter().getText());

        ReagueTrack reagueTrack = reagueTrackRepository.findById(reagueTrackId).orElse(null);
        assert reagueTrack != null;

        Reague reague = reagueTrack.getReague();
        DiscordMember discordMember = discordMemberRepository.findByUserId(Objects.requireNonNull(event.getMember()).getUser().getId()).orElse(null);

        //멤버 등록이 안된 경우
        if(discordMember == null){
            log.error("discordMember is not found!!!");
            //관리자에게 문의 메세지 전송
            userDmSend(event, "시스템에 멤버로 등록되지 않았습니다. 관리자에게 문의해주세요.");
            return;
        }

        //리그 참여자 권한 체크
        boolean isJoinAuth = false;
        for (DiscordUserMension discordUserMension : discordMember.getDiscordUserMensionSet()) {
            if(reague.getJoinAceptMentions().stream().anyMatch(m -> m.getDiscordMention().equals(discordUserMension.getDiscordMention()))){
                isJoinAuth = true;
                break;
            }
        }

        //참여 대상 권한이 아닐 경우 DM발송
        if(!isJoinAuth){
            userDmSend(event, reague.getReagueName() + " 참여 대상이 아닙니다.");
            return;
        }

        //리그 참여 처리
        RegueTrackMember regueTrackMember = regueTrackMemberRepository.findByDiscordMember_UserIdAndReagueTrack_Id(discordMember.getUserId(), reagueTrackId).orElse(null);

        //현재 참여가 안된 경우 참여 등록처리
        ReagueButton joinButton = reague.getReagueButtons().stream().filter(v -> event.getButton().getId().equals(v.getId().toString())).findFirst().orElse(null);
        if(joinButton == null){
            log.error("ReagueButton is not found!!!");
            userDmSend(event, "처리 오류가 발생하였습니다. 관리자에게 문의해주세요.");
            return;
        }

        if(regueTrackMember == null){
            long joinCnt = regueTrackMemberRepository.countByReagueTrack_IdAndJoinType(reagueTrackId, joinButton.getButtonType());
            if(joinCnt >= reague.getJoinMemberLimit()){
                userDmSend(event, "참여 가능 인원이 초과하였습니다.");
                editMessageSend(event, reague);
                return;
            }

            RegueTrackMember newRegueTrackMember = new RegueTrackMember();
            newRegueTrackMember.setReagueTrack(reagueTrack);
            newRegueTrackMember.setDiscordMember(discordMember);
            newRegueTrackMember.setJoinType(joinButton.getButtonType());
            regueTrackMemberRepository.save(newRegueTrackMember);
        }//현재 참여중인 경우 삭제처리
        else{
            //현재 참여 타입과 동일한 타입으로 눌렀을 경우 참여 취소 처리
            if(joinButton.getButtonType().equals(regueTrackMember.getJoinType())){
                regueTrackMemberRepository.delete(regueTrackMember);
            }//현재 참여 타입과 다른 타입으로 신청한 경우 변경처리
            else{
                //참여인원 제한 체크
                long joinCnt = regueTrackMemberRepository.countByReagueTrack_IdAndJoinType(reagueTrackId, joinButton.getButtonType());
                if(joinCnt >= reague.getJoinMemberLimit()){
                    regueTrackMemberRepository.delete(regueTrackMember);
                    userDmSend(event, "참여 가능 인원이 초과하였습니다.");
                    editMessageSend(event, reague);
                    return;
                }

                regueTrackMember.setJoinType(joinButton.getButtonType());
            }
        }

        editMessageSend(event, reague);
    }

    public void editMessageSend(ButtonInteractionEvent event, Reague reague){
        MessageEmbed embed = event.getMessage().getEmbeds().get(0);

        //이벤트 메세지로부터 임베디드 메세지를 받아와 필스를 수정한다.
        EmbedBuilder embedBuilder = new EmbedBuilder(embed);
        embedBuilder.clearFields();

        long reagueTrackId = Long.parseLong(embed.getFooter().getText());
        //카테고리 데이터 생성
        for (ReagueButton reagueButton : reague.getReagueButtons()) {
            List<RegueTrackMember> regueTrackMemberList = regueTrackMemberRepository.findAllByReagueTrack_IdAndJoinType(reagueTrackId, reagueButton.getButtonType());

            String joinMembers = "";
            for (RegueTrackMember trackMember : regueTrackMemberList) {
                String userName = ObjectUtils.isEmpty(trackMember.getDiscordMember().getGlobalName())? trackMember.getDiscordMember().getUsername():trackMember.getDiscordMember().getGlobalName();
                if("".equals(joinMembers)){
                    joinMembers = userName;
                }else {
                    joinMembers += "\n" + userName;
                }
            }

            embedBuilder.addField(String.format("%s(%d/%d)",reagueButton.getButtonName(), regueTrackMemberList.size(), reague.getJoinMemberLimit()), joinMembers, true);
        }

        List<Button> actionButtonList = new ArrayList<>();
        for (ReagueButton reagueButton : reague.getReagueButtons()) {
            if("Primary".equals(reagueButton.getButtonType())){
                actionButtonList.add(Button.primary(String.valueOf(reagueButton.getId()), reagueButton.getButtonName()));
            }else if("Success".equals(reagueButton.getButtonType())){
                actionButtonList.add(Button.success(String.valueOf(reagueButton.getId()), reagueButton.getButtonName()));
            }else if("Secondary".equals(reagueButton.getButtonType())){
                actionButtonList.add(Button.secondary(String.valueOf(reagueButton.getId()), reagueButton.getButtonName()));
            }else{
                actionButtonList.add(Button.danger(String.valueOf(reagueButton.getId()), reagueButton.getButtonName()));
            }
        }

        //수정 메세지 세팅
        MessageEditData messageEditData = new MessageEditBuilder()
                .setEmbeds(embedBuilder.build())
                .setActionRow(actionButtonList)
                .build();

        //메세지 수정 발송
        event.editMessage(messageEditData).queue();
    }

    /**
     * 유저에게 dm 알림
     * @param event
     * @param text
     */
    public void userDmSend(ButtonInteractionEvent event, String text){
        if(event == null || ObjectUtils.isEmpty(text)){
            return;
        }

        event.getMember().getUser().openPrivateChannel().queue(channel -> {
            channel.sendMessage(text).queue();
        });
    }

    /**
     * 오늘 시작되는 리그가 있는지 카운트를 조회한다.
     * @return
     */
    public long countReagueTrackStartToday(){
        return reagueTrackRepository.countAllByTrackDate(LocalDate.now());
    }

    /**
     * 오늘 시작되는 리그트랙 목록을 조회한다.
     * @return
     */
    public List<ReagueTrack> getReagueTrackStartToday(){
        return reagueTrackRepository.findAllByTrackDate(LocalDate.now());
    }
}
