/*
 *  Copyright  2015 Google Inc. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.google.blockly.control;

import android.graphics.Rect;
import android.support.annotation.VisibleForTesting;
import android.util.Pair;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import com.google.blockly.model.Block;
import com.google.blockly.model.Connection;
import com.google.blockly.model.Input;
import com.google.blockly.model.WorkspacePoint;
import com.google.blockly.ui.BlockGroup;
import com.google.blockly.ui.BlockView;
import com.google.blockly.ui.InputView;
import com.google.blockly.ui.ViewPoint;
import com.google.blockly.ui.WorkspaceHelper;
import com.google.blockly.ui.WorkspaceView;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for dragging blocks and groups of blocks within a workspace.
 */
public class Dragger {
    private static final String TAG = "Dragger";

    // Blocks "snap" toward each other at the end of drags if they have compatible connections
    // near each other.  This is the farthest they can snap.
    // Units: Pixels.  TODO: Load from resources, value in dips.
    private static final int MAX_SNAP_DISTANCE = 25;

    private final ViewPoint mDragStart = new ViewPoint();
    private final WorkspacePoint mBlockOriginalPosition = new WorkspacePoint();

    private final ConnectionManager mConnectionManager;
    private final ArrayList<Block> mRootBlocks;
    private final ArrayList<Connection> mDraggedConnections = new ArrayList<>();
    // For use in bumping neighbours; instance variable only to avoid repeated allocation.
    private final ArrayList<Connection> mNeighbouringConnections = new ArrayList<>();
    // Rect for finding the bounding box of the trash can view.
    private final Rect mTrashRect = new Rect();
    // For use in getting location on screen.
    private final int[] mTempArray = new int[2];
    private final ViewPoint mTempViewPoint = new ViewPoint();
    private BlockView mTouchedBlockView;
    private WorkspaceHelper mWorkspaceHelper;
    private WorkspaceView mWorkspaceView;
    private BlockGroup mDragGroup;
    private BlockView mHighlightedBlockView;
    // The view for the trash can.
    private View mTrashView;

    /**
     * @param workspaceHelper For use in computing workspace coordinates.
     * @param connectionManager The {@link ConnectionManager} to update when moving connections.
     * @param rootBlocks The list of blocks to update when moving blocks.
     */
    public Dragger(WorkspaceHelper workspaceHelper, ConnectionManager connectionManager,
                   ArrayList<Block> rootBlocks) {
        mWorkspaceHelper = workspaceHelper;
        mConnectionManager = connectionManager;
        mRootBlocks = rootBlocks;
    }

    /**
     * Start dragging a block in the workspace.
     * <p/>
     * This method separates the block to drag into its own {@link BlockGroup} and sets the initial
     * dragging position. It must be called before any calls to
     * {@link #continueDragging(DragEvent)}, but may not be called immediately on receiving a
     * "down" event (e.g., to first wait for a minimum drag distance).
     *
     * @param blockView The {@link BlockView} to begin dragging.
     * @param startX The x coordinate, in workspace view coordinates, of the touch
     * event that begins the dragging.
     * @param startY The y coordinate, in workspace view coordinates, of the touch
     * event that begins the dragging.
     */
    public void startDragging(BlockView blockView, int startX, int startY) {
        mTouchedBlockView = blockView;
        mBlockOriginalPosition.setFrom(blockView.getBlock().getPosition());
        mDragStart.set(startX, startY);
        setDragGroup(mTouchedBlockView.getBlock());
    }

    /**
     * Continue dragging the currently moving block.
     * <p/>
     * This method must be called for each move event that is received by the {@link WorkspaceView}
     * after {@link #startDragging(BlockView, int, int)} has previously been called.
     *
     * @param event The next move event to handle, as received by the {@link WorkspaceView}.
     */
    public void continueDragging(DragEvent event) {
        updateBlockPosition(event);

        // highlight as we go
        if (mHighlightedBlockView != null) {
            mHighlightedBlockView.setHighlightedConnection(null);
        }
        Pair<Connection, Connection> connectionCandidate = findBestConnection(
                mTouchedBlockView.getBlock());
        if (connectionCandidate != null) {
            mHighlightedBlockView = connectionCandidate.second.getBlock().getView();
            mHighlightedBlockView.setHighlightedConnection(connectionCandidate.second);
        }

        mTouchedBlockView.requestLayout();
    }

    public Block getDragRootBlock() {
        return ((BlockView) (mDragGroup.getChildAt(0))).getBlock();
    }

    /**
     * Finish block dragging.
     * <p/>
     * This method must be called upon receiving the "up" event that ends an ongoing drag process.
     */
    public void finishDragging() {
        if (!snapToConnection(mTouchedBlockView.getBlock())) {
            finalizeMove();
        }
    }

    public void setWorkspaceHelper(WorkspaceHelper helper) {
        mWorkspaceHelper = helper;
    }

    public void setWorkspaceView(WorkspaceView view) {
        mWorkspaceView = view;
    }

    public void setTrashView(View trashView) {
        mTrashView = trashView;
    }

    /**
     * Check whether the given event occurred on top of the trash can button.  Should be called from
     * {@link WorkspaceView}.
     *
     * @param event The event whose location should be checked, with position in WorkspaceView
     * coordinates.
     *
     * @return Whether the event was on top of the trash can button.
     */
    public boolean touchingTrashView(DragEvent event) {
        mTrashView.getLocationOnScreen(mTempArray);
        mTrashView.getHitRect(mTrashRect);

        mTrashRect.offset((mTempArray[0] - mTrashRect.left), (mTempArray[1] - mTrashRect.top));
        // offset drag event positions by the workspace view's position on screen.
        mWorkspaceView.getLocationOnScreen(mTempArray);
        return mTrashRect.contains((int) event.getX() + mTempArray[0],
                (int) event.getY() + mTempArray[1]);
    }

    /**
     * Ends a drag in the trash can, clearing state and deleting blocks as needed.
     */
    public void dropInTrash() {
        if (mHighlightedBlockView != null) {
            mHighlightedBlockView.setHighlightedConnection(null);
            mHighlightedBlockView = null;
        }
        mDraggedConnections.clear();
        mTouchedBlockView = null;
        mWorkspaceView.removeView(mDragGroup);
        mDragGroup = null;
    }

    private void setDragGroup(Block block) {
        BlockView bv = block.getView();
        BlockGroup rootBlockGroup = mWorkspaceHelper.getRootBlockGroup(block);
        BlockGroup bg = (BlockGroup) bv.getParent();
        if (!mRootBlocks.contains(block)) {
            // Child block
            if (block.getPreviousConnection() != null
                    && block.getPreviousConnection().isConnected()) {
                Input in = block.getPreviousConnection().getTargetConnection().getInput();
                if (in == null) {
                    // Next block
                    bg = bg.extractBlocksAsNewGroup(block);
                } else {
                    // Statement input
                    in.getView().unsetChildView();
                }
                block.getPreviousConnection().disconnect();
            } else if (block.getOutputConnection() != null
                    && block.getOutputConnection().isConnected()) {
                // Value input
                Input in = block.getOutputConnection().getTargetConnection().getInput();
                in.getView().unsetChildView();
                block.getOutputConnection().disconnect();
            }
            rootBlockGroup.requestLayout();
            mWorkspaceView.addView(bg);
            mRootBlocks.add(block);
        }
        mDragGroup = bg;
        mDragGroup.bringToFront();

        mDraggedConnections.clear();
        // Don't track any of the connections that we're dragging around.
        block.getAllConnectionsRecursive(mDraggedConnections);
        for (int i = 0; i < mDraggedConnections.size(); i++) {
            mConnectionManager.removeConnection(mDraggedConnections.get(i));
            mDraggedConnections.get(i).setDragMode(true);
        }
    }

    /**
     * Move the currently dragged block in response to a new {@link MotionEvent}.
     * <p/>
     * All of the child blocks move with the root block based on its position during layout.
     *
     * @param event The {@link MotionEvent} to react to.
     */
    private void updateBlockPosition(DragEvent event) {
        int dx = mWorkspaceHelper.virtualViewToWorkspaceUnits((int) (event.getX()) - mDragStart.x);
        int dy = mWorkspaceHelper.virtualViewToWorkspaceUnits((int) (event.getY()) - mDragStart.y);

        if (mWorkspaceHelper.useRtl()) {
            dx *= -1;
        }

        mTouchedBlockView.getBlock().
                setPosition(mBlockOriginalPosition.x + dx, mBlockOriginalPosition.y + dy);
        mDragGroup.requestLayout();
    }

    /**
     * Iterate over all of the connections on the block and find the one that is closest to a
     * valid connection on another block.
     *
     * @param block The {@link Block} whose connections to search.
     *
     * @return A pair of connections, where the first is a connection on {@code block} and the
     * second is the closest compatible connection.
     */
    private Pair<Connection, Connection> findBestConnection(Block block) {
        // Find the connection that is closest to any connection on the block.
        Connection draggedBlockConnection = null;
        Connection compatibleConnection = null;
        double radiusConnection = MAX_SNAP_DISTANCE;
        List<Connection> blockConnections = block.getAllConnections();
        Connection curDraggedBlockConnection;
        Connection curCompatibleConnection;

        for (int i = 0; i < blockConnections.size(); i++) {
            curDraggedBlockConnection = blockConnections.get(i);
            curCompatibleConnection =
                    mConnectionManager.closestConnection(curDraggedBlockConnection,
                            radiusConnection);
            if (curCompatibleConnection != null) {
                draggedBlockConnection = curCompatibleConnection;
                compatibleConnection = curDraggedBlockConnection;
                radiusConnection = draggedBlockConnection.distanceFrom(compatibleConnection);
            }
        }
        if (draggedBlockConnection == null) {
            return null;
        }
        return new Pair<>(compatibleConnection, draggedBlockConnection);
    }

    private boolean snapToConnection(Block block) {
        Pair<Connection, Connection> connectionCandidate = findBestConnection(block);
        if (connectionCandidate == null) {
            return false;
        }

        reconnectViews(connectionCandidate.first, connectionCandidate.second, block);
        finalizeMove();
        return true;
    }

    /**
     * Once the closest connection has been found, call this method to remove the views that are
     * being dragged from the root workspace view and reattach them in the correct places in the
     * view hierarchy, to match the new model.
     *
     * @param movingConnection The connection on the block being moved.
     * @param target The closest compatible connection to movingConnection.
     * @param dragRoot The {@link Block} that is the root of the group of blocks being dragged
     * around.
     */
    @VisibleForTesting
    void reconnectViews(Connection movingConnection, Connection target, Block dragRoot,
                        BlockGroup dragGroup) {
        switch (movingConnection.getType()) {
            case Connection.CONNECTION_TYPE_OUTPUT:
                removeFromRoot(dragRoot, dragGroup);
                connectAsChild(target, movingConnection);
                break;
            case Connection.CONNECTION_TYPE_PREVIOUS:
                removeFromRoot(dragRoot, dragGroup);
                if (target.isStatementInput()) {
                    connectToStatement(target, movingConnection.getBlock());
                } else {
                    connectAfter(target.getBlock(), movingConnection.getBlock());
                }
                break;
            case Connection.CONNECTION_TYPE_NEXT:
                if (!target.isConnected()) {
                    removeFromRoot(target.getBlock());
                }
                if (movingConnection.isStatementInput()) {
                    connectToStatement(movingConnection, target.getBlock());
                } else {
                    connectAfter(movingConnection.getBlock(), target.getBlock());
                }
                break;
            case Connection.CONNECTION_TYPE_INPUT:
                if (!target.isConnected()) {
                    removeFromRoot(target.getBlock());
                }
                connectAsChild(movingConnection, target);
                break;
            default:
                break;
        }
    }

    private void reconnectViews(Connection movingConnection, Connection target, Block dragRoot) {
        reconnectViews(movingConnection, target, dragRoot, mDragGroup);
        // Update the drag group so that everything that has been changed will be properly
        // invalidated.
        mDragGroup = mWorkspaceHelper.getRootBlockGroup(target.getBlock());
    }

    /**
     * Connect a block to a statement input of another block and update views as necessary.  If the
     * statement input already is connected to another block, splice the inferior block between
     * them.
     *
     * @param parentStatementConnection The {@link Connection} on the superior block to be
     * connected to.  Must be on a statement input.
     * @param toConnect The {@link Block} to connect to the statement input.
     */
    private void connectToStatement(Connection parentStatementConnection, Block toConnect) {
        // If there was already a block connected there.
        if (parentStatementConnection.isConnected()) {
            Block remainderBlock = parentStatementConnection.getTargetBlock();
            parentStatementConnection.getInputView().unsetChildView();
            parentStatementConnection.disconnect();
            // We may be dragging multiple blocks.  Try to connect after the end of the group we are
            // dragging.
            Block lastBlockInGroup = mWorkspaceHelper.getNearestParentBlockGroup(toConnect)
                    .lastChildBlock();
            if (lastBlockInGroup.getNextConnection() != null) {
                connectAfter(lastBlockInGroup, remainderBlock);
            } else {
                // Nothing to connect to.  Bump and add to root.
                addToRoot(remainderBlock,
                        mWorkspaceHelper.getNearestParentBlockGroup(remainderBlock));
                bumpBlock(parentStatementConnection, remainderBlock.getPreviousConnection());
            }
        }
        connectAsChild(parentStatementConnection, toConnect.getPreviousConnection());
    }

    /**
     * Connect a block after another block in the same block group.  Updates views as necessary.  If
     * the superior block already has a "next" block, splices the inferior block between the
     * superior block and its "next" block.
     * <p/>
     * Assumes that the inferior's previous connection is disconnected.
     * Assumes that inferior's blockGroup doesn't currently live at the root level.
     *
     * @param superior The {@link Block} after which the inferior block is connecting.
     * @param inferior The {@link Block} to be connected as the superior block's "next" block.
     */
    private void connectAfter(Block superior, Block inferior) {
        BlockGroup superiorBlockGroup = mWorkspaceHelper.getNearestParentBlockGroup(superior);
        BlockGroup inferiorBlockGroup = mWorkspaceHelper.getNearestParentBlockGroup(inferior);

        // To splice between two blocks, just need another call to connectAfter.
        if (superior.getNextConnection().isConnected()) {
            Block remainderBlock = superior.getNextBlock();
            BlockGroup remainderGroup = superiorBlockGroup.extractBlocksAsNewGroup(
                    remainderBlock);
            superior.getNextConnection().disconnect();
            // We may be dragging multiple blocks.  Try to connect after the end of the group we are
            // dragging.
            Block lastBlockInGroup = inferiorBlockGroup.lastChildBlock();
            if (lastBlockInGroup.getNextConnection() != null) {
                connectAfter(lastBlockInGroup, inferiorBlockGroup, remainderBlock, remainderGroup);
            } else {
                // Nothing to connect to.  Bump and add to root.
                addToRoot(remainderBlock, remainderGroup);
                bumpBlock(inferior.getPreviousConnection(), remainderBlock.getPreviousConnection());
            }
        }

        connectAfter(superior, superiorBlockGroup, inferior, inferiorBlockGroup);
    }

    /**
     * Connects two blocks together in a previous-next relationship and merges the
     * {@link BlockGroup} of the inferior block into the {@link BlockGroup} of the superior block.
     *
     * @param superior The {@link Block} that the inferior block is moving to attach to.
     * @param superiorBlockGroup The {@link BlockGroup} belonging to the superior block.
     * @param inferior The {@link Block} that will follow immediately after the superior block.
     * @param inferiorBlockGroup The {@link BlockGroup} belonging to the inferior block.
     */
    private void connectAfter(Block superior, BlockGroup superiorBlockGroup, Block inferior,
                              BlockGroup inferiorBlockGroup) {
        // The superior's next connection and the inferior's previous connections must already be
        // disconnected.
        superior.getNextConnection().connect(inferior.getPreviousConnection());
        superiorBlockGroup.moveBlocksFrom(inferiorBlockGroup, inferior);
    }

    /**
     * Connect a block or block group to an input on another block and update views as necessary.
     * If the input was already connected, splice the child block or group in.
     *
     * @param parent The {@link Connection} on the superior block to connect to.  Must be an input.
     * @param child The {@link Connection} on the inferior block.  Must be an output or previous
     * connection.
     */
    private void connectAsChild(Connection parent, Connection child) {
        InputView parentInputView = parent.getInputView();
        if (parentInputView == null) {
            throw new IllegalStateException("Tried to connect as a child, but the parent didn't "
                    + "have an input view.");
        }

        BlockGroup childBlockGroup = mWorkspaceHelper.getNearestParentBlockGroup(child.getBlock());

        if (parent.isConnected()) {
            Connection remainderConnection = parent.getTargetConnection();
            BlockGroup remainderGroup = (BlockGroup) parentInputView.getChildView();
            parent.disconnect();
            parentInputView.unsetChildView();
            // Traverse the tree to ensure it doesn't branch. We only reconnect if there's a single
            // place it could be rebased to.
            Connection lastInputConnection = childBlockGroup.getLastInputConnection();
            if (lastInputConnection != null) {
                connectAsChild(lastInputConnection, remainderConnection);
            } else {
                // Bump and add back to root.
                Block remainderBlock = remainderConnection.getBlock();
                addToRoot(remainderBlock, remainderGroup);
                bumpBlock(parent, remainderConnection);
            }
        }
        parent.connect(child);
        parentInputView.setChildView(childBlockGroup);
    }


    /**
     * Removes the given block and its view from the root view.  If it didn't live at the root level
     * do nothing.
     *
     * @param block The {@link Block} to look up and remove.
     */
     private void removeFromRoot(Block block) {
        BlockGroup group = mWorkspaceHelper.getNearestParentBlockGroup(block);
        if (group.getParent() instanceof WorkspaceView) {
            // The block we are connecting to is a root block.
            removeFromRoot(block, group);
        }
    }

    /**
     * Removes the given block and its view from the root view.  The block must live at the root
     * level.
     *
     * @param block The {@link Block} to remove.
     * @param group The {@link BlockGroup} to remove.
     */
    private void removeFromRoot(Block block, BlockGroup group) {
        if (group == null) {
            removeFromRoot(block);
        } else {
            mWorkspaceView.removeView(group);
            mRootBlocks.remove(block);
        }
    }

    private void addToRoot(Block block, BlockGroup group) {
        mRootBlocks.add(block);
        mWorkspaceView.addView(group);
    }

    /**
     * Update the positions of all of the connections that were impacted by the move and add them
     * back to the manager.
     */
    private void finalizeMove() {
        if (mHighlightedBlockView != null) {
            mHighlightedBlockView.setHighlightedConnection(null);
            mHighlightedBlockView = null;
        }
        BlockGroup rootBlockGroup = mWorkspaceHelper.getRootBlockGroup(
                mTouchedBlockView.getBlock());
        bumpNeighbours(mTouchedBlockView.getBlock(), rootBlockGroup);
        // All of the connection locations will be set relative to their block views immediately
        // after this loop.  For now we just want to unset drag mode and add the connections back
        // to the list; 0, 0 is a cheap place to put them.
        for (int i = 0; i < mDraggedConnections.size(); i++) {
            Connection cur = mDraggedConnections.get(i);
            cur.setPosition(0, 0);
            cur.setDragMode(false);
            mConnectionManager.addConnection(cur);
        }
        mDraggedConnections.clear();

        rootBlockGroup.requestLayout();
    }

    private void bumpBlock(Connection staticConnection, Connection impingingConnection) {
        BlockGroup toBump = mWorkspaceHelper.getRootBlockGroup(impingingConnection.getBlock());
        bumpBlock(staticConnection, impingingConnection, toBump);

    }

    private void bumpBlock(Connection staticConnection, Connection impingingConnection,
                           BlockGroup impingingBlockGroup) {
        // TODO (rohlfingt): Adapt to RTL
        int dx = (staticConnection.getPosition().x + MAX_SNAP_DISTANCE)
                - impingingConnection.getPosition().x;
        int dy = (staticConnection.getPosition().y + MAX_SNAP_DISTANCE)
                - impingingConnection.getPosition().y;
        Block rootBlock = ((BlockView) impingingBlockGroup.getChildAt(0)).getBlock();
        rootBlock.setPosition(rootBlock.getPosition().x + dx, rootBlock.getPosition().y + dy);
        impingingBlockGroup.bringToFront();
        impingingBlockGroup.updateAllConnectorLocations();
        mWorkspaceView.requestLayout();
    }

    /**
     * Move all neighbours of the current block and its sub-blocks so that they don't appear to
     * be connected to the current block.
     *
     * @param currentBlock The {@link Block} to bump others away from.
     */
    private void bumpNeighbours(Block currentBlock, BlockGroup rootBlockGroup) {
        List<Connection> connectionsOnBlock = new ArrayList<>();
        rootBlockGroup.updateAllConnectorLocations();
        // Move this block before trying to bump others
        Connection prev = currentBlock.getPreviousConnection();
        if (prev != null && !prev.isConnected()) {
            bumpInferior(rootBlockGroup, prev);
        }
        Connection out = currentBlock.getOutputConnection();
        if (out != null && !out.isConnected()) {
            bumpInferior(rootBlockGroup, out);
        }

        currentBlock.getAllConnections(connectionsOnBlock);
        for (int i = 0; i < connectionsOnBlock.size(); i++) {
            Connection conn = connectionsOnBlock.get(i);
            if (conn.isHighPriority()) {
                if (conn.isConnected()) {
                    bumpNeighbours(conn.getTargetBlock(), rootBlockGroup);
                }
                bumpConnectionNeighbours(conn, rootBlockGroup);
            }
        }
    }

    /**
     * Bump the block containing {@code lowerPriority} away from the first nearby block it finds.
     *
     * @param rootBlockGroup The root block group of the block being bumped.
     * @param lowerPriority The low priority connection that is the center of the current bump
     * operation.
     */
    private void bumpInferior(BlockGroup rootBlockGroup, Connection lowerPriority) {
        mConnectionManager.getNeighbours(lowerPriority, MAX_SNAP_DISTANCE,
                mNeighbouringConnections);
        // Bump from the first one that isn't in the same block group.
        for (int j = 0; j < mNeighbouringConnections.size(); j++) {
            Connection curNeighbour = mNeighbouringConnections.get(j);
            if (mWorkspaceHelper.getRootBlockGroup(curNeighbour.getBlock()) != rootBlockGroup) {
                bumpBlock(curNeighbour, lowerPriority, rootBlockGroup);
                return;
            }
        }
    }

    /**
     * Find all connections near a given connection and bump their blocks away.
     *
     * @param conn The high priority connection that is at the center of the current bump
     * operation.
     * @param rootBlockGroup The root block group of the block conn belongs to.
     */
    private void bumpConnectionNeighbours(Connection conn, BlockGroup rootBlockGroup) {
        mConnectionManager.getNeighbours(conn, MAX_SNAP_DISTANCE, mNeighbouringConnections);
        for (int j = 0; j < mNeighbouringConnections.size(); j++) {
            Connection curNeighbour = mNeighbouringConnections.get(j);
            BlockGroup neighbourBlockGroup = mWorkspaceHelper.getRootBlockGroup(
                    curNeighbour.getBlock());
            if (neighbourBlockGroup != rootBlockGroup) {
                bumpBlock(conn, curNeighbour, neighbourBlockGroup);
            }
        }
    }
}