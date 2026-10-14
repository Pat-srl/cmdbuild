package org.cmdbuild.api.fluent;

import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.size;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.cmdbuild.api.fluent.FluentApiExecutor.AdvanceProcess;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExecutorBasedFluentApiTest {

    private static final String CLASS_NAME = "class";
    private static final String DOMAIN_NAME = "domain";
    private static final String FUNCTION_NAME = "function";
    private static final String REPORT_NAME = "report";
    private static final String REPORT_FORMAT = "xyz";
    private static final String PROCESS_CLASS_NAME = "processclass";

    private static final long CARD_ID = 42l;
    private static final String PROCESS_INSTANCE_ID = "XYZ";

    private static final CardDescriptor CARD_DESCRIPTOR = new CardDescriptorImpl(CLASS_NAME, CARD_ID);
    private static final ProcessInstanceDescriptor PROCESS_INSTANCE_DESCRIPTOR = new ProcessInstanceDescriptorImpl(PROCESS_CLASS_NAME, CARD_ID, PROCESS_INSTANCE_ID);

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Captor
    public ArgumentCaptor<Iterable<? extends AttachmentDescriptor>> attachmentDescriptorsCaptor;

    @Captor
    public ArgumentCaptor<Iterable<? extends Attachment>> attachmentCaptor;

    private FluentApiExecutor executor;
    private FluentApi api;

    @Before
    public void createApi() throws Exception {
        executor = mock(FluentApiExecutor.class);
        api = new ExecutorBasedFluentApi(executor);
    }

    @Test
    public void executorCalledWhenCreatingNewCard() throws Exception {
        final NewCard newCard = api.newCard(CLASS_NAME);

        when(executor.create(newCard)).thenReturn(CARD_DESCRIPTOR);

        assertThat(newCard.create(), equalTo(CARD_DESCRIPTOR));

        verify(executor).create(newCard);
        verifyNoMoreInteractions(executor);
    }

    @Test
    public void executorCalledWhenUpdatingExistingCard() throws Exception {
        final ExistingCard existingCard = api.existingCard(CLASS_NAME, CARD_ID);
        existingCard.update();

        verify(executor).update(existingCard);
        verifyNoMoreInteractions(executor);
    }

    @Test
    public void executorCalledWhenDeletingExistingCard() throws Exception {
        final ExistingCard existingCard = api.existingCard(CLASS_NAME, CARD_ID);
        existingCard.delete();

        verify(executor).delete(existingCard);
        verifyNoMoreInteractions(executor);
    }

    @Test
    public void executorCalledWhenFetchingExistingCard() throws Exception {
        final ExistingCard existingCard = api.existingCard(CLASS_NAME, CARD_ID);
        existingCard.fetch();

        verify(executor).fetch(existingCard);
        verifyNoMoreInteractions(executor);
    }

    @Test
    public void executorCalledWhenFetchingClass() throws Exception {
        final QueryClass queryClass = api.queryClass(CLASS_NAME);
        queryClass.fetch();

        verify(executor).fetchCards(queryClass);
        verifyNoMoreInteractions(executor);
    }

    @Test
    public void executorCalledWhenCreatingNewRelation() throws Exception {
        final NewRelation newRelation = api.newRelation(DOMAIN_NAME);
        newRelation.create();

        verify(executor).create(newRelation);
        verifyNoMoreInteractions(executor);
    }

    @Test
    public void executorCalledWhenDeletingExistingRelation() throws Exception {
        final ExistingRelation existingRelation = api.existingRelation(DOMAIN_NAME);
        existingRelation.delete();

        verify(executor).delete(existingRelation);
        verifyNoMoreInteractions(executor);
    }

    @Test
    public void executorCalledWhenFetchingRelations() throws Exception {
        final ActiveQueryRelations query = api.queryRelations(CLASS_NAME, CARD_ID);
        query.fetch();

        verify(executor).fetch(query);
        verifyNoMoreInteractions(executor);
    }

    @Test
    public void executorCalledWhenExecutingFunctionCall() throws Exception {
        final FunctionCall functionCall = api.callFunction(FUNCTION_NAME);
        functionCall.execute();

        verify(executor).execute(functionCall);
        verifyNoMoreInteractions(executor);
    }

    @Test
    public void executorCalledWhenCreatingReport() throws Exception {
        final CreateReport createReport = api.createReport(REPORT_NAME, REPORT_FORMAT);
        createReport.download();

        verify(executor).download(createReport);
        verifyNoMoreInteractions(executor);
    }

    @Test
    public void executorCalledWhenStartingNewProcessInstance() throws Exception {
        final NewProcessInstance newProcess = api.newProcessInstance(PROCESS_CLASS_NAME);

        when(executor.createProcessInstance(newProcess, AdvanceProcess.NO)).thenReturn(PROCESS_INSTANCE_DESCRIPTOR);

        assertThat(newProcess.start(), sameInstance(PROCESS_INSTANCE_DESCRIPTOR));

        verify(executor).createProcessInstance(newProcess, AdvanceProcess.NO);
        verifyNoMoreInteractions(executor);
    }

    @Test
    public void executorCalledWhenFetchingAttachments() throws Exception {
        // given
        final CardDescriptor source = api.existingCard(CLASS_NAME, CARD_ID);
        final AttachmentDescriptor foo = mock(AttachmentDescriptor.class);
        final AttachmentDescriptor bar = mock(AttachmentDescriptor.class);
        final AttachmentDescriptor baz = mock(AttachmentDescriptor.class);
        final Iterable<AttachmentDescriptor> attachments = asList(foo, bar, baz);
        doReturn(attachments) //
                .when(executor).fetchAttachments(any(CardDescriptor.class));

        // when
        final Iterable<AttachmentDescriptor> fetched = api.existingCard(source) //
                .attachments() //
                .fetch();

        assertThat(fetched, equalTo(attachments));

        verify(executor).fetchAttachments(eq(source));
        verifyNoMoreInteractions(executor);
    }

    @Test
    public void executorCalledWhenUploadingMultipleAttachments() throws Exception {
        // given
        final CardDescriptor source = api.existingCard(CLASS_NAME, CARD_ID);
        final Attachment foo = mock(Attachment.class);
        final Attachment bar = mock(Attachment.class);
        final Attachment baz = mock(Attachment.class);

        // when
        api.existingCard(source) //
                .attachments() //
                .upload(foo, bar, baz);

        verify(executor).upload(eq(source), eq(asList(foo, bar, baz)));
        verifyNoMoreInteractions(executor);
    }

    @Test
    public void executorCalledWhenUploadingSingleAttachment() throws Exception {
        // given
        final CardDescriptor source = api.existingCard(CLASS_NAME, CARD_ID);
        final String url = temporaryFolder.newFile().toURI().toURL().toString();

        // when
        api.existingCard(source) //
                .attachments() //
                .upload("foo", "this is foo", "some category", url);

        verify(executor).upload(eq(source), attachmentCaptor.capture());
        verifyNoMoreInteractions(executor);

        final Iterable<? extends Attachment> captured = attachmentCaptor.getValue();
        assertThat(size(captured), equalTo(1));
        assertThat(get(captured, 0).getName(), equalTo("foo"));
    }

    @Test
    public void executorCalledWhenDeletingAttachments() throws Exception {
        // given
        final CardDescriptor source = api.existingCard(CLASS_NAME, CARD_ID);
        final AttachmentDescriptor foo = mock(AttachmentDescriptor.class);
        final AttachmentDescriptor bar = mock(AttachmentDescriptor.class);
        final AttachmentDescriptor baz = mock(AttachmentDescriptor.class);
        final Iterable<AttachmentDescriptor> attachments = asList(foo, bar, baz);
        doReturn(attachments) //
                .when(executor).fetchAttachments(any(CardDescriptor.class));

        // when
        api.existingCard(source) //
                .attachments() //
                .selectAll() //
                .delete();

        verify(executor).fetchAttachments(eq(source));
        verify(executor).delete(eq(source), attachmentDescriptorsCaptor.capture());
        verifyNoMoreInteractions(executor);

        final Iterable<? extends AttachmentDescriptor> captured = attachmentDescriptorsCaptor.getValue();
        assertThat(get(captured, 0), equalTo(foo));
        assertThat(get(captured, 1), equalTo(bar));
        assertThat(get(captured, 2), equalTo(baz));
    }

    @Test
    public void executorCalledWhenDownloadingAttachments() throws Exception {
        // given
        final CardDescriptor source = api.existingCard(CLASS_NAME, CARD_ID);
        final AttachmentDescriptor foo = mock(AttachmentDescriptor.class);
        final AttachmentDescriptor bar = mock(AttachmentDescriptor.class);
        final AttachmentDescriptor baz = mock(AttachmentDescriptor.class);
        final Iterable<AttachmentDescriptor> attachments = asList(foo, bar, baz);
        doReturn(attachments) //
                .when(executor).fetchAttachments(any(CardDescriptor.class));
        final Attachment downloaded_foo = mock(Attachment.class);
        final Attachment downloaded_bar = mock(Attachment.class);
        final Attachment downloaded_baz = mock(Attachment.class);
        final Iterable<Attachment> downloaded_attachments = asList(downloaded_foo, downloaded_bar, downloaded_baz);
        doReturn(downloaded_attachments) //
                .when(executor).download(any(CardDescriptor.class), any(Iterable.class));

        // when
        final Iterable<Attachment> downloaded = api.existingCard(source) //
                .attachments() //
                .selectAll() //
                .download();

        assertThat(get(downloaded, 0), equalTo(downloaded_foo));
        assertThat(get(downloaded, 1), equalTo(downloaded_bar));
        assertThat(get(downloaded, 2), equalTo(downloaded_baz));

        verify(executor).fetchAttachments(eq(source));
        verify(executor).download(eq(source), attachmentDescriptorsCaptor.capture());
        verifyNoMoreInteractions(executor);

        final Iterable<? extends AttachmentDescriptor> captured = attachmentDescriptorsCaptor.getValue();
        assertThat(get(captured, 0), equalTo(foo));
        assertThat(get(captured, 1), equalTo(bar));
        assertThat(get(captured, 2), equalTo(baz));
    }

    @Test
    public void executorCalledWhenCopyingAttachments() throws Exception {
        // given
        final CardDescriptor source = api.existingCard(CLASS_NAME, CARD_ID);
        final AttachmentDescriptor foo = mock(AttachmentDescriptor.class);
        final AttachmentDescriptor bar = mock(AttachmentDescriptor.class);
        final AttachmentDescriptor baz = mock(AttachmentDescriptor.class);
        final Iterable<AttachmentDescriptor> attachments = asList(foo, bar, baz);
        doReturn(attachments) //
                .when(executor).fetchAttachments(any(CardDescriptor.class));
        final CardDescriptor destination = api.existingCard(CLASS_NAME + "2", CARD_ID * 10);

        // when
        api.existingCard(source) //
                .attachments() //
                .selectAll() //
                .copyTo(destination);

        verify(executor).fetchAttachments(eq(source));
        verify(executor).copy(eq(source), attachmentDescriptorsCaptor.capture(), eq(destination));
        verifyNoMoreInteractions(executor);

        final Iterable<? extends AttachmentDescriptor> captured = attachmentDescriptorsCaptor.getValue();
        assertThat(get(captured, 0), equalTo(foo));
        assertThat(get(captured, 1), equalTo(bar));
        assertThat(get(captured, 2), equalTo(baz));
    }

    @Test
    public void executorCalledWhenMovingAttachments() throws Exception {
        // given
        final CardDescriptor source = api.existingCard(CLASS_NAME, CARD_ID);
        final AttachmentDescriptor foo = mock(AttachmentDescriptor.class);
        final AttachmentDescriptor bar = mock(AttachmentDescriptor.class);
        final AttachmentDescriptor baz = mock(AttachmentDescriptor.class);
        final Iterable<AttachmentDescriptor> attachments = asList(foo, bar, baz);
        doReturn(attachments) //
                .when(executor).fetchAttachments(any(CardDescriptor.class));
        final CardDescriptor destination = api.existingCard(CLASS_NAME + "2", CARD_ID * 10);

        // when
        api.existingCard(source) //
                .attachments() //
                .selectAll() //
                .moveTo(destination);

        verify(executor).fetchAttachments(eq(source));
        verify(executor).move(eq(source), attachmentDescriptorsCaptor.capture(), eq(destination));
        verifyNoMoreInteractions(executor);

        final Iterable<? extends AttachmentDescriptor> captured = attachmentDescriptorsCaptor.getValue();
        assertThat(get(captured, 0), equalTo(foo));
        assertThat(get(captured, 1), equalTo(bar));
        assertThat(get(captured, 2), equalTo(baz));
    }

}
